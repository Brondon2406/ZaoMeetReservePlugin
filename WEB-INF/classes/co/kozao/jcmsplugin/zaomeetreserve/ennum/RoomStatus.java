package co.kozao.jcmsplugin.zaomeetreserve.ennum;

	public enum RoomStatus {
		AVAILABLE("Disponible"),
	    OCCUPIED("Occupée"),
	    MAINTENANCE("Maintenance");
	
	    private final String label;
	
	    RoomStatus(String label) {
	        this.label = label;
	    }
	
	    public String getLabel() {
	        return label;
	    }
	
	    
	    public static RoomStatus fromRaw(String raw) {
	        if (raw == null) {
	            return AVAILABLE;
	        }
	        String value = raw.trim();

	        // Valeurs du champ enumerate de Room.xml
	        switch (value.toLowerCase()) {
	            case "value1":
	                return AVAILABLE;
	            case "value2":
	                return OCCUPIED;
	            case "value3":
	                return MAINTENANCE;
	            default:
	                break;
	        }

	        // Noms de l'enum (filtres : available, occupied, maintenance)
	        for (RoomStatus status : values()) {
	            if (status.name().equalsIgnoreCase(value)) {
	                return status;
	            }
	        }
	        return AVAILABLE;
	    }
	}