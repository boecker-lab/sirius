/*
 *  This file is part of the SIRIUS Software for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer, Marvin Meusel and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with SIRIUS.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */

package de.unijena.bioinf.ms.middleware;

import de.unijena.bioinf.ChemistryBase.jobs.SiriusJobs;
import de.unijena.bioinf.auth.AuthService;
import de.unijena.bioinf.auth.AuthServices;
import de.unijena.bioinf.jjobs.JJob;
import de.unijena.bioinf.jjobs.JobSubmitter;
import de.unijena.bioinf.jjobs.ProgressJJob;
import de.unijena.bioinf.jjobs.SwingJobManager;
import de.unijena.bioinf.ms.annotations.PrintCitations;
import de.unijena.bioinf.ms.frontend.Run;
import de.unijena.bioinf.ms.frontend.SiriusCLIApplication;
import de.unijena.bioinf.ms.frontend.core.ApplicationCore;
import de.unijena.bioinf.ms.frontend.core.Workspace;
import de.unijena.bioinf.ms.frontend.subtools.CLIRootOptions;
import de.unijena.bioinf.ms.frontend.subtools.ToolChainJob;
import de.unijena.bioinf.ms.frontend.subtools.config.DefaultParameterConfigLoader;
import de.unijena.bioinf.ms.frontend.subtools.middleware.MiddlewareAppOptions;
import de.unijena.bioinf.ms.frontend.workflow.InstanceBufferFactory;
import de.unijena.bioinf.ms.frontend.workflow.SimpleInstanceBuffer;
import de.unijena.bioinf.ms.frontend.workflow.WorkflowBuilder;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.middleware.service.gui.GuiService;
import de.unijena.bioinf.ms.middleware.service.projects.ProjectsProvider;
import de.unijena.bioinf.ms.properties.PropertyManager;
import de.unijena.bioinf.projectspace.ProjectSpaceManagerFactory;
import de.unijena.bioinf.projectspace.SiriusProjectSpaceManagerFactory;
import de.unijena.bioinf.rest.ProxyManager;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.context.WebServerPortFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition
@Slf4j
public class SiriusMiddlewareApplication extends SiriusCLIApplication implements CommandLineRunner, DisposableBean {
    private final static ProjectSpaceManagerFactory<?> psf = new SiriusProjectSpaceManagerFactory();
    private final static InstanceBufferFactory<?> bufferFactory = (bufferSize, instances, tasks, dependJob, progressSupport) ->
            new SimpleInstanceBuffer(bufferSize, instances, tasks, dependJob, progressSupport, new JobSubmitter() {
                @Override
                public <Job extends JJob<Result>, Result> Job submitJob(Job j) {
                    if (j instanceof ToolChainJob<?> tj) {
                        Jobs.submit((ProgressJJob<?>) j, j::identifier, tj::getProjectName, tj::getToolName);
                        return j;
                    } else {
                        return Jobs.MANAGER().submitJob(j);
                    }
                }
            });
    private final static MiddlewareAppOptions<?> middlewareOpts = new MiddlewareAppOptions<>();

    private final ApplicationContext appContext;

    @Bean
    public ProjectSpaceManagerFactory<?> projectSpaceManagerFactory() {
        return psf;
    }

    @Bean
    public InstanceBufferFactory<?> instanceBufferFactory() {
        return bufferFactory;
    }

    public SiriusMiddlewareApplication(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    public static void main(String[] args) {
        //start gui as default if no command is given
        if (args == null || args.length == 0)
            args = new String[]{"rest", "-s", "--gui"};

        //check if service mode is used before command line is really parsed to decide whether we need to
        //configure a spring app or not.
        if (Arrays.stream(args).anyMatch(it ->
                it.equalsIgnoreCase(MiddlewareAppOptions.class.getAnnotation(CommandLine.Command.class).name())
                        || Arrays.stream(MiddlewareAppOptions.class.getAnnotation(CommandLine.Command.class).aliases())
                        .anyMatch(cmd -> cmd.equalsIgnoreCase(it))
                        || it.equalsIgnoreCase("-h") || it.equalsIgnoreCase("--help") // just to get Middleware help.
        )) {

            try {
                System.setProperty("de.unijena.bioinf.sirius.springSupport", "true");
                System.setProperty(APP_TYPE_PROPERTY_KEY, "SERVICE");

                measureTime("Init Swing Job Manager");
                // The spring app classloader seems not to be correctly inherited to sub thread
                // So we need to ensure that the apache.configuration2 libs gets access otherwise.
                // SwingJobManager is needed to show loading screens in GUI
                SiriusJobs.setJobManagerFactory((cpuThreads) -> new SwingJobManager(
                        cpuThreads,
                        Math.min(PropertyManager.getNumberOfThreads(), 4),
                        Thread.currentThread().getContextClassLoader()
                ));

                ApplicationCore.DEFAULT_LOGGER.info("Starting Application Core");
                PropertyManager.setProperty("de.unijena.bioinf.sirius.BackgroundRuns.autoremove", "false");

                //parse args before spring app starts so we can manipulate app behaviour via command line
                CLIRootOptions rootOptions = new CLIRootOptions(new DefaultParameterConfigLoader(), psf);
                measureTime("init Run");
                RUN = new Run(new WorkflowBuilder(rootOptions, bufferFactory, List.of(middlewareOpts)));
                measureTime("Start Parse args");
                RUN.parseArgs(args);
                measureTime("Parse args Done");


                // decides whether the app runs infinitely
                WebApplicationType webType = WebApplicationType.SERVLET;
                measureTime("Configure Boot Environment");
                //configure boot app
                final SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(SiriusMiddlewareApplication.class)
                        .web(webType)
                        .headless(webType.equals(WebApplicationType.NONE))
                        .bannerMode(Banner.Mode.OFF);

                measureTime("Start Workflow");
                SpringApplication app = appBuilder.application();
                app.addListeners(new ApplicationPidFileWriter(Workspace.WORKSPACE.resolve("sirius.pid").toFile()));
                app.addListeners(new WebServerPortFileWriter(Workspace.WORKSPACE.resolve("sirius.port").toFile()));
                app.run(args);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);// Zero because this is the help message case
            }
        } else {
            SiriusCLIApplication.runMain(args, List.of());
        }
    }


    @Override
    public void run(String... args) {
        middlewareOpts.setProjectsProvider(appContext.getBean(ProjectsProvider.class));
        middlewareOpts.setGuiService(appContext.getBean(GuiService.class));

        successfulParsed = RUN.makeWorkflow() != null;
        measureTime("Parse args Done!");

        if (successfulParsed) {
            RUN.compute();
            System.err.println("SIRIUS Service started successfully!");
        } else {
            System.exit(0);// Zero because this is the help message case
        }
    }

    @Override
    public void destroy() {
        log.info("SIRIUS is cleaning up threads and shuts down...");
        // ensure that token is not in bad state after shut down.
        try {
            AuthService as = ApplicationCore.WEB_API.getAuthService();
            if (as.isLoggedIn())
                AuthServices.writeRefreshToken(ApplicationCore.WEB_API.getAuthService(), ApplicationCore.TOKEN_FILE, true);
            else
                Files.deleteIfExists(ApplicationCore.TOKEN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ProxyManager.disconnect();
        }

        ApplicationCore.DEFAULT_LOGGER.info("CLI shut down hook: SIRIUS is cleaning up threads and shuts down...");
        try {
            if (RUN != null) {
                RUN.cancel();
            }
        } finally {
            if (successfulParsed && PropertyManager.DEFAULTS.createInstanceWithDefaults(PrintCitations.class).value)
                ApplicationCore.BIBTEX.citeToSystemErr();
        }
    }
}