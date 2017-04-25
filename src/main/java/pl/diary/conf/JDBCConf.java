/*
 * The MIT License
 *
 * Copyright 2017 Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.diary.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
@author Micha� Szyma�ski, kontakt: michal.szymanski.aajar@gmail.comł Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Configuration
public class JDBCConf {

    @Bean
    @Qualifier("dataSource")
    public javax.sql.DataSource dataSource(){

        Properties properties = getProperties();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(properties.getProperty("jdbc.driver"));
        ds.setUrl(properties.getProperty("jdbc.url"));
        ds.setUsername(properties.getProperty("jdbc.username"));
        ds.setPassword(properties.getProperty("jdbc.password"));
        return ds;
    }

    private Properties getProperties(){
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("sample.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().toGenericString()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }

     @Bean
     @Qualifier("baza")
    public NamedParameterJdbcTemplate getJdbcTemplate(DataSource data){
        return new NamedParameterJdbcTemplate(data);
    }


}
