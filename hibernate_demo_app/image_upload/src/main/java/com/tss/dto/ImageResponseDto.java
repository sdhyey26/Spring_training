package com.tss.dto;

public class ImageResponseDto {
	    private String name;
	    private String type;
	    private byte[] data;

	    public ImageResponseDto(String name, String type, byte[] data) {
	        this.name = name;
	        this.type = type;
	        this.data = data;
	    }

	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public String getType() { return type; }
	    public void setType(String type) { this.type = type; }

	    public byte[] getData() { return data; }
	    public void setData(byte[] data) { this.data = data; }
}
