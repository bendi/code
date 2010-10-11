package pl.bedkowski.code.filterdef;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mock_sub_object")
public class MockSubObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public MockSubObject() {
	}
	
	public MockSubObject(String name) {
		this.name = name;
	}
	
    private Long id = null;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public Long getId() {
		return id;
	}
    
    public void setId(Long id) {
		this.id = id;
	}
    
    private String name;
    
    @Column(name="name", length=50)
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
    
    private MockObject mockObject;
    
    @ManyToOne(fetch = FetchType.EAGER)
    public MockObject getMockObject() {
		return mockObject;
	}
    
    public void setMockObject(MockObject mockObject) {
		this.mockObject = mockObject;
	}
}
