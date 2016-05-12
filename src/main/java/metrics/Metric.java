/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassMetrics.java,v 1.12 2007/07/25 12:24:00 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package metrics;

public class Metric {
    private final int wmc;
    private final int noc;
    private final int rfc;
    private final int cbo;
    private final int dit;
    private final int lcom;

    public Metric(int wmc, int noc, int rfc, int cbo, int dit, int lcom) {
        this.wmc = wmc;
        this.noc = noc;
        this.rfc = rfc;
        this.cbo = cbo;
        this.dit = dit;
        this.lcom = lcom;
    }

    public int getWmc() {
        return wmc;
    }

    public int getNoc() {
        return noc;
    }

    public int getRfc() {
        return rfc;
    }

    public int getCbo() {
        return cbo;
    }

    public int getDit() {
        return dit;
    }

    public int getLcom() {
        return lcom;
    }


}
