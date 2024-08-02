package fr.maxlego08.items.zcore.enums;

public enum Folder {

	UTILS,

	;
	

	public String toFolder(){
		return name().toLowerCase();
	}
	
}
