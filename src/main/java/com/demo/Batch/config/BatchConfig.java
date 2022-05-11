package com.demo.Batch.config;

import com.demo.Batch.Model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

     @Autowired
    private DataSource dataSource;   //info stored in app.properties

     @Autowired
     private JobBuilderFactory jobBuilderFactory;   //to create object of job

     @Autowired
     private StepBuilderFactory stepBuilderFactory;     //to create step

     @Bean
     public FlatFileItemReader<User> reader(){
            FlatFileItemReader<User> reader=new FlatFileItemReader<>();   //tells how we will read file
            reader.setResource(new ClassPathResource("household-living-costs.csv"));
            reader.setLineMapper(getLineMapper());   //how to map line tht is being read
            reader.setLinesToSkip(1);    //if error comes then skip tht line
            return reader;
     }

    private LineMapper<User> getLineMapper() {

        DefaultLineMapper<User> lineMapper= new DefaultLineMapper<>();
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();  //for mapping
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();

        lineTokenizer.setNames(new String[]{"ItemGroup","ItemName","Weight","Cost"}); //Nme of column in csv
        lineTokenizer.setIncludedFields(new int[]{0,4,11,12});
        fieldSetMapper.setTargetType(User.class);

        lineMapper.setFieldSetMapper(fieldSetMapper);   // to set fields of object
        lineMapper.setLineTokenizer(lineTokenizer);    //to seprate commas frm input string or seprate the raed line into tokens
        return lineMapper;
    }

    @Bean
    public UserItemProcessor processor(){      //configuring processr
      return new UserItemProcessor();

    }

    @Bean
    public JdbcBatchItemWriter<User> writer(){       //we have to write in jdbc as we r using jdbc
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());  //properties in user class will be set as property
        writer.setSql("insert into user(itemGroup,itemName,weight,cost) values(:itemGroup, :itemName, :weight, :cost)");
        writer.setDataSource(this.dataSource);
        return  writer;
    }

    @Bean
    public Job importUserJob(){
        return this.jobBuilderFactory.get("User-Import-Job").incrementer(new RunIdIncrementer()).flow(step1()).next(step1()).end().build();  //can't create obj of job so need to use jobbuilderfactory
    }

    @Bean
    public Step step1() {
    return this.stepBuilderFactory.get("step1").<User,User>chunk(10).reader(reader()).processor(processor()).writer(writer()).build();
    }

}


//spring.batch.jdbc.initialize-schema=always    //all springbatch schema will be  initialized automtcly