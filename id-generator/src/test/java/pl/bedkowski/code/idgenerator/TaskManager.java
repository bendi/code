package pl.bedkowski.code.idgenerator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class TaskManager {

    public static void main(String[] args) {
        TaskManager mgr = new TaskManager();

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("thePersistenceUnit");
        EntityManager theManager = factory.createEntityManager();

        for (int i = 0; i < 10; i++) {
            mgr.createAndStoreTasks(theManager);
        }

        theManager.getTransaction().begin();
        TypedQuery<Client> tq = theManager.createQuery("from Client", Client.class);
        List<Client> clients = tq.getResultList();
        for (Client c : clients) {
            for (Project p : c.getProjects()) {
                if (p.getTasks().get(0).getTaskNumber() == null || p.getTasks().get(0).getTaskNumber().getId() == null) {
                    continue;
                }
                System.out.println(p.getId() + " " + p.getTasks().get(0).getTaskNumber().getId().getTaskNum());
            }
        }
    }

    private void createAndStoreTasks(EntityManager em) {
        em.getTransaction().begin();

        Client c = new Client();
        {
            Project p = new Project();
            p.setClient(c);
            List<Project> projects = new ArrayList<Project>();
            projects.add(p);
            c.setProjects(projects);

            List<Task> tasks = new ArrayList<Task>();

            {
                Task t = new Task();
                t.setProject(p);
                TaskNumber tn = new TaskNumber();
                tn.setProject(p);
                t.setTaskNumber(tn);

                tasks.add(t);
            }

            p.setTasks(tasks);
        }

        em.persist(c);

        em.getTransaction().commit();
    }
}