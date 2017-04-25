/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class Errors {
@Controller
public class ErrorsControler implements ErrorController{

    final String errorPath = "/error";
    @RequestMapping(value= errorPath, method=RequestMethod.GET)
    @ResponseBody
    public String handle(Model model, HttpServletRequest rq, HttpServletResponse rs){
        int errorCode = rs.getStatus();

        StringBuilder builder = new StringBuilder();
        builder.append("Error " + errorCode);
        builder.append("<br>");
        builder.append(HttpStatus.valueOf(errorCode).getReasonPhrase());
        return builder.toString();
    }

        @Override
        public String getErrorPath() {
            return errorPath;
        }

}
}
