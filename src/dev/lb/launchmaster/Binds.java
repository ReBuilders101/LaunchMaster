package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
@interface Binds {
	Bind[] value();
}
