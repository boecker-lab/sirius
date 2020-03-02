package de.unijena.bioinf.ms.gui.tree_viewer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import netscape.javascript.JSObject;

public class TreeViewerBridge {

    static public final Map<String, String> COLOR_VARIANTS = new HashMap<String,
        String>() {{
            put("none", "none");
            put("mass deviation in m/z", "md_mz");
            put("mass deviation in mz (absolute)", "md_mz_abs");
            put("mass deviation in ppm", "md_ppm");
            put("mass deviation in ppm (absolute)", "md_ppm_abs");
            put("relative intensity", "rel_int");
        }};
    static public final String[] COLOR_VARIANTS_DESC = {
        "none", "mass deviation in m/z", "mass deviation in mz (absolute)",
        "mass deviation in ppm", "mass deviation in ppm (absolute)",
        "relative intensity" };
    static public final String[] COLOR_VARIANTS_IDS = {
        "none", "md_mz", "md_mz_abs", "md_ppm", "md_ppm_abs", "rel_int" };

    static public final String[] COLOR_VARIANTS_2 = {"md_mz_abs", "md_ppm_abs",
                                                     "rel_int" };
    static public final String[] COLOR_VARIANTS_3 = { "md_mz", "md_ppm" };

    static public final String[] COLOR_SCHEMES_2 = {"blues", "greens", "reds" };
    static public final String[] COLOR_SCHEMES_3 = { "red to blue", "brown to turquoise" };

    static public final String COLOR_SCHEME_2 = "blues";
    static public final String COLOR_SCHEME_3 = "red to blue";

    static public final String[] NODE_ANNOTATIONS = {
        "m/z", "mass deviation in m/z", "mass deviation in ppm", "score",
        "relative intensity" };
    static public final String[] NODE_ANNOTATIONS_IDS = {
        "mz", "massDeviationMz", "massDeviationPpm", "score",
        "relativeIntensity" };

    public static final int TREE_SCALE_MIN = 25;
    public static final int TREE_SCALE_MAX = 200;
    public static final int TREE_SCALE_INIT = 100;

    TreeViewerBrowser browser;

    public String color_scheme_2_selected;
    public String color_scheme_3_selected;

    public TreeViewerBridge(TreeViewerBrowser browser) {
        this.browser = browser;
        this.color_scheme_2_selected = COLOR_SCHEME_2;
        this.color_scheme_3_selected = COLOR_SCHEME_3;
    }

    public String functionString(String function, String... args) {
        return function + "(" + String.join(", ", args) + ");";
    }

    public void updateConfig(TreeConfig config){
        JSObject win = (JSObject) browser.executeJS("window");
        win.setMember("config", config);
    }

    public void settingsChanged(){
        browser.executeJS("settingsChanged()");
    }

    public void scaleTree(float mag) {
        browser.executeJS(functionString("scaleTree", String.valueOf(mag)));
    }

    public float getTreeScale() {
        // can be either Integer or Double
        // NOTE: this value is 1/tree_scale!
        return ((Number) browser.executeJS("tree_scale")).floatValue();
    };

    public float getTreeScaleMin() {
        // can be either Integer or Double
        return ((Number) browser.executeJS("tree_scale_min")).floatValue();
    }

    public void resetTree() {
        browser.executeJS("reset();");
    }

    public void resetZoom(){
        browser.executeJS("resetZoom();");
    }

    public String getSVG() {
        // NOTE: PDF export does not support the style 'text-anchor'
        // with this first function coordinates for all texts with this
        // style will be recalculated to the same effect as using the style
        browser.executeJS("realignAllText()");
        String svg = (String) browser.executeJS("getSVGString()");
        browser.executeJS("drawTree()");
        return svg;
    }

    public String getJSONTree(){
        return (String) browser.executeJS("getJSONTree()");
    }

    // capabilities below are handled by TreeConfig now
    @Deprecated
    public void colorCode(String color_variant) {
        String color_scheme;
        String variant_id = COLOR_VARIANTS.get(color_variant);
        if (Arrays.asList(COLOR_VARIANTS_2).contains(variant_id))
            color_scheme = color_scheme_2_selected;
        else
            color_scheme = color_scheme_3_selected;
        String function_string = functionString("colorCode", "'" + variant_id + "'", "'" + color_scheme + "'");
        browser.executeJS(function_string);
    }

    @Deprecated
    public void setColorBarVis(boolean visible) {
        browser.executeJS(functionString("toggleColorBar", visible ? "true" : "false"));
    }

    @Deprecated
    public void setNodeAnnotations(List<String> nodeAnnotations) {
        int nodeLabelIndex = -1;
        for (int i = 0; i < nodeAnnotations.size(); i++)
            if (nodeAnnotations.get(i).equals("molecular formula"))
                nodeLabelIndex = i;
        browser.executeJS("toggleNodeLabels(" + ((nodeLabelIndex != -1) ? "true" : "false") + ")");
        if (nodeLabelIndex != -1)
            nodeAnnotations.remove(nodeLabelIndex);
        browser.setJSArray("annot_fields", nodeAnnotations.toArray(new String[0]));
        browser.executeJS("updateBoxHeight()");

    }

    @Deprecated
    public String[] getNodeAnnotations() {
        return (String[]) browser.getJSArray("annot_fields");
    }

    @Deprecated
    public void setEdgeLabel(boolean enabled) {
        browser.executeJS(functionString("showEdgeLabels", enabled ? "true" : "false"));
    }
}