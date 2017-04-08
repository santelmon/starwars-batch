package com.starwars.batch.config;

import com.starwars.batch.domain.People;
import com.starwars.batch.listener.BatchListener;
import com.starwars.batch.listener.StepListener;
import com.starwars.batch.processor.PeopleProcessor;
import com.starwars.batch.repository.PeopleRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class Csv2XmlBatchConfiguration {

  @Autowired
  private PeopleRepository peopleRepository;

  @Autowired
  private BatchListener listener;

  @Autowired
  private StepListener stepListener;

  @Bean
  public ItemReader<People> peopleReader(){
    FlatFileItemReader<People> itemReader = new FlatFileItemReader<>();

    itemReader.setResource(new FileSystemResource("src/main/resources/people.csv"));

    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setNames(new String[] {"name","birthYear","gender","height","mass","eyeColor","hairColor","skinColor"});
    BeanWrapperFieldSetMapper<People> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(People.class);

    DefaultLineMapper<People> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(lineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    itemReader.setLineMapper(lineMapper);

    return itemReader;
  }

  @Bean
  public ItemWriter<People> getPeopleWriter(){
    RepositoryItemWriter<People> itemWriter = new RepositoryItemWriter<>();

    itemWriter.setRepository(peopleRepository);
    itemWriter.setMethodName("save");

    return itemWriter;
  }

  @Bean
  public ItemProcessor<People,People> getPeopleProcessor(){
    return new PeopleProcessor();
  }

  @Bean
  public Step getCsvStep(StepBuilderFactory builderFactory,
                         ItemReader peopleReader,
                         ItemProcessor peopleProcessor,
                         ItemWriter peopleWriter){
    return builderFactory
      .get("csvStep")
      .chunk(10)
      .listener(stepListener)
      .reader(peopleReader)
      .processor(peopleProcessor)
      .writer(peopleWriter)
      .build();
  }

  @Bean
  public Job job(JobBuilderFactory jobBuilderFactory, Step csvStep){
    return jobBuilderFactory
      .get("job")
      .incrementer(new RunIdIncrementer())
      .listener(listener)
      .start(csvStep)
      .build();
  }
}
