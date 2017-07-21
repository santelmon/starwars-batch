package com.starwars.config;

import com.starwars.batch.domain.People;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.ItemWriter;

@Configuration
@EnableBatchProcessing
public class Csv2XmlBatchConfiguration {


    @Bean
    public ItemReader<People> peopleItemReader() {

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames( new String[] {"name", "bithYear", "gender", "height", "mass", "eyeColor", "hairColor", "skinColor"});

        BeanWrapperFieldSetMapper<People> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(People.class);

        DefaultLineMapper<People> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(lineTokenizer);

        FlatFileItemReader<People> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/people.csv"));
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);
    }

    @Bean
    public ItemWriter<People> peopleItemWriter() {
        StaxEventItemWriter<People> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/people-resources.csv"));
        itemWriter.setRootTagName("peoples");
        itemWriter.setOverwriteOutput(true);

    }

    @Bean
    public Step csvStep(StepBuilderFactory stepBuilderFactory, ItemReader peopleReader, ItemWriter peopleWriter) {

        return stepBuilderFactory
                .get("csvStep")
                .chunk(10)
                .reader(peopleReader)
                .writer(peopleWriter)
                .build();
    }

    @Bean
    public Job csvJob(JobBuilderFactory jobBuilderFactory, )
}
