package sol.funny.demobatch.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import sol.funny.datacore.entity.domain.Client;
import sol.funny.demobatch.JobCompletionNotificationListener;
import sol.funny.demobatch.bean.ClientBean;
import sol.funny.demobatch.processor.ClientItemProcessor;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class ClientImportJobConfiguration implements ApplicationContextAware {

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    public JobExplorer jobExplorer;

    @Autowired
    protected JobCompletionNotificationListener listener;

    @Autowired
    public JobRegistry jobRegistry;

    @Autowired
    DataSource dataSource;

    private ApplicationContext applicationContext;

    @Bean
    public Job clientImportJob() {
        return  jobBuilderFactory.get("clientImportJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importUserStep())
                .end()
                .build();
    }

    @Bean
    @Qualifier(value = "importUserStep")
    public Step importUserStep() {
        return stepBuilderFactory.get("importUserStep")
                .<ClientBean, Client> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public ClientItemProcessor processor() {
        return new ClientItemProcessor();
    }

    @Bean
    public FlatFileItemReader<ClientBean> reader() {
        return new FlatFileItemReaderBuilder<ClientBean>()
                .name("clientItemReader")
                .resource(new ClassPathResource("client.csv"))
                .delimited()
                .names(new String[]{"userName", "password"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<ClientBean>() {{
                    setTargetType(ClientBean.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Client> writer() {
        return new JdbcBatchItemWriterBuilder<Client>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO CLIENT (RECORD_NO,USER_NAME,PASSWORD,STATUS,LAST_UPDATE_DATE) " +
                "VALUES (SEQ_CLIENT.NEXTVAL, :userName, :password, :status, SYSDATE)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JobLauncher simpleJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor registrar = new JobRegistryBeanPostProcessor();

        registrar.setJobRegistry(this.jobRegistry);
        registrar.setBeanFactory(this.applicationContext.getAutowireCapableBeanFactory());
        registrar.afterPropertiesSet();

        return registrar;
    }

    @Bean
    public JobOperator jobOperator() throws Exception {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();

        simpleJobOperator.setJobLauncher(simpleJobLauncher());
        simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        simpleJobOperator.setJobRepository(this.jobRepository);
        simpleJobOperator.setJobExplorer(this.jobExplorer);
        simpleJobOperator.setJobRegistry(this.jobRegistry);
        simpleJobOperator.afterPropertiesSet();

        return simpleJobOperator;
    }
}
