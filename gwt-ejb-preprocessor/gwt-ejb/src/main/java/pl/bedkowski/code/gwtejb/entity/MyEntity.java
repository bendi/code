package pl.bedkowski.code.gwtejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class MyEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@NotNull
	@Size(min = 1, max = 335)
	@Column(length = 335)
	private String name;

	@NotNull
	@Max(50)
	private Integer shoeSize;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getShoeSize() {
		return shoeSize;
	}

	public void setShoeSize(Integer shoeSize) {
		this.shoeSize = shoeSize;
	}
}
