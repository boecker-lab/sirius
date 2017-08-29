package de.unijena.bioinf.sirius.gui.fingerid;

import de.unijena.bioinf.ChemistryBase.fp.ProbabilityFingerprint;

public class CanopusResult {
    protected ProbabilityFingerprint canopusFingerprint;

    public ProbabilityFingerprint getCanopusFingerprint() {
        return canopusFingerprint;
    }

    public CanopusResult(ProbabilityFingerprint canopusFingerprint) {
        this.canopusFingerprint = canopusFingerprint;
    }
}
