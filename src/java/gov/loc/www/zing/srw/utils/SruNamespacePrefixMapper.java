/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package gov.loc.www.zing.srw.utils;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


/**
 * @author Michael Hoppe
 * 
 * Sets prefixes to elements while marshalling
 *
 */
public class SruNamespacePrefixMapper extends NamespacePrefixMapper {
	
	public static final String[] SRW_NAMESPACE = new String[]{"http://www.loc.gov/zing/srw/", "sru-zr"};

	public static final String[] SRW_DIAGNOSTIC_NAMESPACE = new String[]{"http://www.loc.gov/zing/srw/diagnostic/", "sru-diagnostic"};

	public static final String[] SRW_XCQL_NAMESPACE = new String[]{"http://www.loc.gov/zing/cql/xcql/", "sru-xcql"};

	public static final String[] SRW_EXTRA_DATA_NAMESPACE = new String[]{"http://oclc.org/srw/extraData/", "sru-extradata"};

	/* (non-Javadoc)
	 * @see com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper#getPreferredPrefix(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public String getPreferredPrefix(final String namespaceUri,
			final String suggestion,
			final boolean requirePrefix) {
		if (SRW_NAMESPACE[0].equals(namespaceUri)) {
			return SRW_NAMESPACE[1];
		}
		else if (SRW_DIAGNOSTIC_NAMESPACE[0].equals(namespaceUri)) {
			return SRW_DIAGNOSTIC_NAMESPACE[1];
		}
		else if (SRW_XCQL_NAMESPACE[0].equals(namespaceUri)) {
			return SRW_XCQL_NAMESPACE[1];
		}
		else if (SRW_EXTRA_DATA_NAMESPACE[0].equals(namespaceUri)) {
			return SRW_EXTRA_DATA_NAMESPACE[1];
		}
		else {
			return null;
		}
	}
}
