package co.kozao.jcmsplugin.zaomeetreserve.util;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class CalendarViewModel {

    private CalendarViewModel() {
    }

    /** Un creneau reserve, positionne sur la frise horaire (8h-18h par defaut). */
    public static class TimelineBlock {
        private final String label;
        private final double startHour;
        private final double endHour;
        private final String type; // confirmed | pending | unavailable
        private final boolean past;

        /**
         * @param label        libelle affiche dans le bloc
         * @param startHour    heure de debut (0-24)
         * @param endHour      heure de fin (0-24)
         * @param type         confirmed | pending | unavailable
         * @param blockEndDate date/heure reelle de fin du creneau, utilisee pour
         *                     calculer isPast() ; peut etre null (ex: bloc "salle
         *                     en maintenance" qui ne represente pas une reservation
         *                     precise), auquel cas isPast() renvoie toujours false.
         */
        public TimelineBlock(String label, double startHour, double endHour, String type, Date blockEndDate) {
            this.label = label;
            this.startHour = startHour;
            this.endHour = endHour;
            this.type = type;
            this.past = blockEndDate != null && blockEndDate.before(new Date());
        }

        /**
         * Conserve pour compatibilite : pas de date reelle disponible, donc isPast()
         * renverra toujours false pour ce bloc.
         */
        public TimelineBlock(String label, double startHour, double endHour, String type) {
            this(label, startHour, endHour, type, null);
        }

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }

        public boolean isPast() {
            return past;
        }

        public double getLeftPercent() {
            double span = ZaoMeetReserveConstants.CALENDAR_TIMELINE_END_HOUR - ZaoMeetReserveConstants.CALENDAR_TIMELINE_START_HOUR;
            return Math.max(0, ((startHour - ZaoMeetReserveConstants.CALENDAR_TIMELINE_START_HOUR) / span) * 100);
        }

        public double getWidthPercent() {
            double span = ZaoMeetReserveConstants.CALENDAR_TIMELINE_END_HOUR - ZaoMeetReserveConstants.CALENDAR_TIMELINE_START_HOUR;
            return Math.max(2, ((endHour - startHour) / span) * 100);
        }
    }

    /** Une ligne de frise : soit une salle (vue Jour), soit un jour de semaine (vue Hebdomadaire). */
    public static class TimelineRow {
        private final String label;
        private final List<TimelineBlock> blocks = new ArrayList<>();

        public TimelineRow(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public List<TimelineBlock> getBlocks() {
            return blocks;
        }

        public void addBlock(TimelineBlock block) {
            blocks.add(block);
        }
    }

    /** Une cellule du calendrier mensuel. */
    public static class MonthCell {
        private final int dayNumber;
        private final String dateStr; // yyyy-MM-dd, pour le lien vers la modale du jour
        private final int reservationCount;
        private final Date cellDate; // date reelle representee par la cellule, pour isPast()

        public MonthCell(int dayNumber, String dateStr, int reservationCount) {
            this(dayNumber, dateStr, reservationCount, null);
        }

        /**
         * @param cellDate date reelle du jour represente par la cellule ; peut etre
         *                 null pour les cellules de "remplissage" (jours du mois
         *                 precedent/suivant affiches vides), auquel cas isPast()
         *                 renvoie false.
         */
        public MonthCell(int dayNumber, String dateStr, int reservationCount, Date cellDate) {
            this.dayNumber = dayNumber;
            this.dateStr = dateStr;
            this.reservationCount = reservationCount;
            this.cellDate = cellDate;
        }

        public int getDayNumber() {
            return dayNumber;
        }

        public String getDateStr() {
            return dateStr;
        }

        public int getReservationCount() {
            return reservationCount;
        }

        public boolean hasReservations() {
            return reservationCount > 0;
        }

        public boolean isPast() {
            if (dayNumber == 0 || cellDate == null) {
                return false;
            }
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return cellDate.before(today.getTime());
        }
    }
}