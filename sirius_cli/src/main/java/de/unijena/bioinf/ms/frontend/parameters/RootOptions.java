package de.unijena.bioinf.ms.frontend.parameters;

import de.unijena.bioinf.ms.io.projectspace.SiriusProjectSpace;

import java.io.IOException;

public interface RootOptions extends InputProvider {

    Integer getMaxInstanceBuffer();

    Integer getInitialInstanceBuffer();

    SiriusProjectSpace getProjectSpace() throws IOException;



    /*final class IO {
        public final SiriusProjectSpace projectSpace;
        public final Iterator<ExperimentResult> inputIterator;

        public IO(SiriusProjectSpace projectSpace, Iterator<ExperimentResult> inputIterator) {
            this.projectSpace = projectSpace;
            this.inputIterator = inputIterator;
        }
    }*/
}
