package jobs;

import models.Author;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        if(Author.count()==0) {
            Fixtures.loadModels("data.yml");
        }
    }
}
