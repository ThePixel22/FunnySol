package sol.funny.demobatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class JobLauncherController {
    @Autowired
    private JobOperator jobOperator;

    @GetMapping(value = "/abc")
    public Long batch() throws Exception {
        Long jobExecutionId = this.jobOperator.start("clientImportJob", "name=hungngu");
        log.info("Job Execution Id : {}", jobExecutionId);
        return jobExecutionId;
    }


}
