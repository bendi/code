package pl.bedkowski.code.tapestry.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Color {

	public static final Map<Long, Color> COLORS = new LinkedHashMap<Long, Color>();

	static {
		COLORS.put(1L, new Color(1L, "blue"));
		COLORS.put(2L, new Color(2L, "green"));
	}

	private String label;
	private Long id;

	public Color(){}

	public Color(Long id,String label) {
		this.id = id;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}



}
