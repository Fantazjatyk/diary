/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.dao.user;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Repository
public class UserDetailsExtractor {

    @Autowired
    NamedParameterJdbcTemplate template;

    public Map getDetails(String id){
       String statement = "select * from diary_users_settings natural join diary_users where user_id = :user_id";
       Map map = new HashMap();
       map.put("user_id", id);

       Map result = null;

       try{
            result = template.queryForMap(statement, map);
       }
       catch(EmptyResultDataAccessException e){

       }
       return result;
    }
}
