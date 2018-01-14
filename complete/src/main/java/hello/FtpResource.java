package hello;

public class FtpResource {
	
	String name;
	boolean isFolder;
	String lastModifiedDate;
	
	public FtpResource() {
		
	}
	
	public FtpResource(String name, boolean isFolder) {
		this.name = name;
		this.isFolder = isFolder;
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
