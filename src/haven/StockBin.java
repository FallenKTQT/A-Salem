/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

public class StockBin extends Widget implements DTarget {
    static Tex bg = Resource.loadtex("gfx/hud/bosq");
    static Text.Foundry lf;
    private Indir<Resource> res;
    private Text label;
    private int rem=0,bi=0;
    static {
        lf = new Text.Foundry(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18), java.awt.Color.WHITE);
        lf.aa = true;
    }
    
    @Widget.RName("spbox")
	public static class $_ implements Widget.Factory {
	    public Widget create(Coord c, Widget parent, Object[] args) {
		return(new StockBin(c, parent, parent.ui.sess.getres((Integer)args[0]), (Integer)args[1], (Integer)args[2]));
	    }
	}
    
    private void setlabel(int rem, int bi) {
	this.rem = rem;
	this.bi = bi;
        label = lf.renderf("%d/%d", rem, bi);
    }
    
    public StockBin(Coord c, Widget parent, Indir<Resource> res, int rem, int bi) {
        super(c, bg.sz(), parent);
        this.res = res;
        setlabel(rem, bi);
    }
    
    public void draw(GOut g) {
        g.image(bg, Coord.z);
	try {
            Tex t = res.get().layer(Resource.imgc).tex();
            Coord dc = new Coord(6, (bg.sz().y / 2) - (t.sz().y / 2));
            g.image(t, dc);
        } catch(Loading exc) {
	}
        g.image(label.tex(), new Coord(40, (bg.sz().y / 2) - (label.tex().sz().y / 2)));
    }
    
    public Object tooltip(Coord c, Widget prev) {
	try {
	    if(res.get().layer(Resource.tooltip) != null)
		return(res.get().layer(Resource.tooltip).t);
	} catch(Loading e) {
	}
	return(null);
    }
    
    public boolean mousedown(Coord c, int button) {
	if(button == 1) {
	    if(ui.modshift ^ ui.modctrl){	//SHIFT or CTRL means pull
		int dir = ui.modctrl?-1:1;	//CTRL means pull out, SHIFT pull in
		int all = (dir > 0)?bi-rem:rem;	//count depends on direction
		int k = ui.modmeta?all:1;	//ALT means pull all
		for(int i=0; i<k; i++){wdgmsg("xfer2", dir, 1);} //modflags set to 1 to emulate only SHIFT pressed
	    } else {
		wdgmsg("click");
	    }
	    return(true);
	}
	return(false);
    }
    
    public boolean mousewheel(Coord c, int amount) {
	if(amount < 0)
	    wdgmsg("xfer2", -1, ui.modflags());
	if(amount > 0)
	    wdgmsg("xfer2", 1, ui.modflags());
	return(true);
    }
    
    public boolean drop(Coord cc, Coord ul) {
        wdgmsg("drop");
        return(true);
    }
    
    public boolean iteminteract(Coord cc, Coord ul) {
        wdgmsg("iact");
        return(true);
    }
    
    public void uimsg(String msg, Object... args) {
        if(msg == "chnum") {
            setlabel((Integer)args[0], (Integer)args[1]);
	} else if(msg == "chres") {
	    res = ui.sess.getres((Integer)args[0]);
        } else {
            super.uimsg(msg, args);
        }
    }
}
