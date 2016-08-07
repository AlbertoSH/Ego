package com.github.albertosh.ego.generator;

import javax.lang.model.element.TypeElement;

public interface IEgoGenerator {

    public void generate(TypeElement classElement);

}
