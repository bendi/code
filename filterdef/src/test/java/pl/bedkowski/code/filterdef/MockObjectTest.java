package pl.bedkowski.code.filterdef;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Rule;
import org.junit.Test;

import pl.bedkowski.code.filterdef.MockObject;
import pl.bedkowski.code.filterdef.MockSubObject;
import pl.bedkowski.code.filterdef.rule.InitHibernateRule;

import com.google.common.collect.Sets;


public class MockObjectTest {
	
	@Rule
	public InitHibernateRule hibernateUtil = new InitHibernateRule();
	
	@Test
	public void testFindByNameFilter() throws Exception {
    	{
	        Session session = hibernateUtil.getSessionFactory().getCurrentSession();
	        
			Transaction transaction = session.beginTransaction();
	 
	        MockObject object0 = new MockObject();
	        session.save(object0);
	        
	        Set<MockSubObject> msos = Sets.newHashSet();
	        {
	        	MockSubObject mso = new MockSubObject("name1");
	        	mso.setMockObject(object0);
	        	msos.add(mso);
	        }
	        {
	        	MockSubObject mso = new MockSubObject("name2");
	        	mso.setMockObject(object0);
	        	msos.add(mso);
	        }
	        
	        object0.setSubObjects(msos);
	        
	        MockObject object1 = new MockObject();
	        session.save(object1);
	        
	        session.flush();
	
	        transaction.commit();
    	}
        Session session = hibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        Criteria crit = session.createCriteria(MockObject.class)
        	.add(Restrictions.eq("id", 1L))
        	.addOrder(Order.asc("id"));

        session.enableFilter("findByName").setParameter("name", "name1");
        
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        List<MockObject> lMo = list(crit);
        
        assertFalse("List is empty", lMo.isEmpty());
        assertEquals("Should be 1 elements in the list", 1, lMo.size());
        MockObject mo = lMo.get(0);
        assertNotNull("List contains null element.", mo);
        assertEquals("Id should be equal to one", Long.valueOf(1L), mo.getId());
        Set<MockSubObject> lMso = mo.getSubObjects();
        assertFalse("List is empty", lMso.isEmpty());
        assertEquals("There should be one element in the list.", 1, lMso.size());
        
        transaction.commit();
	}
	
    
    @SuppressWarnings("unchecked")
	private static <T> List<T> list(Criteria crit) {
    	return (List<T>) crit.list();
    }	

}
