/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.diary.notes.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import pl.diary.notes.Note;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class TitleOrContentValidator implements ConstraintValidator<TitleOrContent, Note>{

    @Override
    public void initialize(TitleOrContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(Note value, ConstraintValidatorContext context) {
       boolean result = !(value.getContent().isEmpty()) || !(value.getTitle().isEmpty());
       return result;
    }

}
