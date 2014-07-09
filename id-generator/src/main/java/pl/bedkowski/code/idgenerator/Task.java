package pl.bedkowski.code.idgenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Task extends IdLong {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project    project;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumns({ @JoinColumn(name = "project_id", referencedColumnName = "project_id"),
            @JoinColumn(name = "task_number", referencedColumnName = "task_num") })
    private TaskNumber taskNumber;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public TaskNumber getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(TaskNumber taskNumber) {
        this.taskNumber = taskNumber;
        this.taskNumber.setTask(this);
    }
}
