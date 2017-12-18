/*
 * tksCommons
 * 
 * Author : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de) License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.bean;

import java.io.Serializable;

import de.thksystems.util.lang.ObjectUtils;

/**
 * Basic bean implementing toString(), hashCode() and equals() by reflection.
 *
 */
public abstract class ReflectiveBasicBean implements Serializable {

	private static final long serialVersionUID = 3931002492014094338L;

	@Override
	public String toString() {
		return ObjectUtils.buildToStringReflectiveConsideringAnnotations(this);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.buildHashCodeReflectiveConsideringAnnotations(this);
	}

	@Override
	public boolean equals(Object obj) {
		//@formatter:off
		return obj != null 
				&& obj.getClass().equals(this.getClass()) 
				&& ObjectUtils.buildEqualsReflectiveConsideringAnnotations(this, obj);
		//@formatter:on
	}

}
