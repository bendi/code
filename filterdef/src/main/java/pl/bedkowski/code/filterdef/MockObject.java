package pl.bedkowski.code.filterdef;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "mock_object")
@FilterDef(name = "findByName", parameters = { @ParamDef(name = "name", type = "string") })
public class MockObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id = null;

	private int version = 0;

	private Set<MockSubObject> subObjects;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Version
	@Column(name = "version")
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	@OneToMany(mappedBy = "mockObject", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Filter(name = "findByName", condition = "(name = :name)")
	public Set<MockSubObject> getSubObjects() {
		return subObjects;
	}

	public void setSubObjects(Set<MockSubObject> subObjects) {
		this.subObjects = subObjects;
	}
}