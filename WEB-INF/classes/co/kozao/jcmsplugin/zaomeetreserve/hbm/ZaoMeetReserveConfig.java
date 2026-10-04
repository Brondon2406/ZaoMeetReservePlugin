package co.kozao.jcmsplugin.zaomeetreserve.hbm;

import java.io.Serializable;

public class ZaoMeetReserveConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long openinghour;
    private Long closinghour;
    private int minslotduration;
    private int maxslotduration;
    private int maxbookingdelaydays;
    private int autocancelminutes;
    private boolean recuring;
    private int recurringmaxperweek;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


	public Long getOpeninghour() {
		return openinghour;
	}
	public void setOpeninghour(Long openinghour) {
		this.openinghour = openinghour;
	}
	public Long getClosinghour() {
		return closinghour;
	}
	public void setClosinghour(Long closinghour) {
		this.closinghour = closinghour;
	}
	public int getMinslotduration() {
		return minslotduration;
	}
	public void setMinslotduration(int minslotduration) {
		this.minslotduration = minslotduration;
	}
	public int getMaxslotduration() {
		return maxslotduration;
	}
	public void setMaxslotduration(int maxslotduration) {
		this.maxslotduration = maxslotduration;
	}
	public int getMaxbookingdelaydays() {
		return maxbookingdelaydays;
	}
	public void setMaxbookingdelaydays(int maxbookingdelaydays) {
		this.maxbookingdelaydays = maxbookingdelaydays;
	}
	public int getAutocancelminutes() {
		return autocancelminutes;
	}
	public void setAutocancelminutes(int autocancelminutes) {
		this.autocancelminutes = autocancelminutes;
	}
	public boolean isRecuring() {
		return recuring;
	}
	public void setRecuring(boolean recuring) {
		this.recuring = recuring;
	}
	public int getRecurringmaxperweek() {
		return recurringmaxperweek;
	}
	public void setRecurringmaxperweek(int recurringmaxperweek) {
		this.recurringmaxperweek = recurringmaxperweek;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
	@Override
	public String toString() {
	    return "ZaoMeetReserveConfig [openinghour=" + openinghour
	            + ", closinghour=" + closinghour
	            + ", minslotduration=" + minslotduration
	            + ", maxslotduration=" + maxslotduration
	            + ", maxbookingdelaydays=" + maxbookingdelaydays
	            + ", autocancelminutes=" + autocancelminutes
	            + ", recuring=" + recuring
	            + ", recurringmaxperweek=" + recurringmaxperweek + "]";
	}
	

}