package pl.bedkowski.code.idgenerator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class TaskNumber {

    @EmbeddedId
    @GeneratedValue(generator = "task_number")
    @GenericGenerator(name = "task_number", strategy = "pl.bedkowski.code.idgenerator.TaskNumberGenerator")
    private Id     id = new Id();

    @Column
    private String prefix;

    @OneToOne(mappedBy = "taskNumber")
    private Task   task;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setProject(Project p) {
        id.setProject(p);
    }

    @Embeddable
    public static class Id implements Serializable {

        private static final long serialVersionUID = 1L;

        @ManyToOne
        @JoinColumn(name = "project_id")
        private Project           project;

        @Column(name = "task_num", updatable = false)
        private Long              taskNum;

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public Long getTaskNum() {
            return taskNum;
        }

        public void setTaskNum(Long taskNum) {
            this.taskNum = taskNum;
        }

    }

}
