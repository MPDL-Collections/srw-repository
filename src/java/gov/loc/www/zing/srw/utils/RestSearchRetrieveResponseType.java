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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


import ORG.oclc.os.SRW.SRWDatabase;
import ORG.oclc.os.SRW.SRWDiagnostic;
import de.escidoc.core.common.util.Constants;
import de.escidoc.core.domain.sru.SearchRetrieveResponseType;

/**
 * @author Michael Hoppe
 *
 */
public class RestSearchRetrieveResponseType {
	private SearchRetrieveResponseType searchRetrieveResponse;

	private List<Stream> recordStreams;

    private Marshaller marshaller = null;

	private Pattern p = Pattern.compile(".*?<\\/([^>]*?)\\:records>.*", Pattern.DOTALL);

    public RestSearchRetrieveResponseType() throws IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance("de.escidoc.core.domain.sru:de.escidoc.core.domain.sru.extradata");
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",        
            	    new SruNamespacePrefixMapper()); 
        } catch (Exception e) {
            throw new IOException(e);
        }

    }
    /**
	 * @return the searchRetrieveResponse
	 */
	public SearchRetrieveResponseType getSearchRetrieveResponse() {
		return searchRetrieveResponse;
	}

	/**
	 * @param searchRetrieveResponse the searchRetrieveResponse to set
	 */
	public void setSearchRetrieveResponse(
			SearchRetrieveResponseType searchRetrieveResponse) {
		this.searchRetrieveResponse = searchRetrieveResponse;
	}

	/**
	 * @return the recordStreams
	 */
	public List<Stream> getRecordStreams() {
		if (recordStreams == null) {
			recordStreams = new ArrayList<Stream>();
		}
		return recordStreams;
	}

	/**
	 * write Object as Stream-Object
	 * 
	 * @param db SRWDatabase
	 * @return Stream search-result stream
	 */
	public Stream toStream(SRWDatabase db, boolean closeRecordStreams) {
		Stream stream = new Stream();
		String searchResponse = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			marshaller
			.marshal(
					new de.escidoc.core.domain.sru.SearchRetrieveResponseTO(
							getSearchRetrieveResponse()), out);
			searchResponse = new String(out.toByteArray(), Constants.XML_CHARACTER_ENCODING);
		} 
		catch (Exception e) {
			return null;
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {}
		}
		try {
			Matcher m = p.matcher(searchResponse);
			String prefix = "";
			if (m.matches()) {
				prefix = m.group(1);
			}
			String[] par = searchResponse.split("<\\/[^>]*?\\:records>");
			if (par.length < 2) {
				stream.write(searchResponse.getBytes(Constants.XML_CHARACTER_ENCODING));
				stream.lock();
				if (closeRecordStreams) {
					for (Stream s : getRecordStreams()) {
						s.close();
					}
				}
				return stream;
			}
			String[] parts = par[0].split("<\\/[^>]*?\\:recordPacking>");
			if (parts.length - 1 != getRecordStreams().size()) {
				throw new IOException("records-length does not match streams-size");
			}
			for (int i = 0; i < getRecordStreams().size(); i++) {
				stream.write(parts[i].getBytes(Constants.XML_CHARACTER_ENCODING));
				stream.write(("</" + prefix + ":recordPacking><" + prefix + ":recordData>").getBytes(Constants.XML_CHARACTER_ENCODING));
				if (closeRecordStreams) {
					IOUtils.copyAndCloseInput(getRecordStreams().get(i).getInputStream(), stream);
				} else {
					IOUtils.copy(getRecordStreams().get(i).getInputStream(), stream);
				}
				stream.write(("</" + prefix + ":recordData>").getBytes(Constants.XML_CHARACTER_ENCODING));
			}
			stream.write(parts[parts.length - 1].getBytes(Constants.XML_CHARACTER_ENCODING));
			stream.write(("</" + prefix + ":records>").getBytes(Constants.XML_CHARACTER_ENCODING));
			stream.write(par[1].getBytes(Constants.XML_CHARACTER_ENCODING));
			stream.lock();
			
			return stream;
		} catch (Exception e) {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ex) {}
			}

			db.diagnostic(SRWDiagnostic.GeneralSystemError, e.toString(),
					getSearchRetrieveResponse());
			try {
				ByteArrayOutputStream errOut = new ByteArrayOutputStream();
				marshaller
				.marshal(
						new de.escidoc.core.domain.sru.SearchRetrieveResponseTO(
								getSearchRetrieveResponse()), errOut);
				searchResponse = new String(errOut.toByteArray(), Constants.XML_CHARACTER_ENCODING);
				Stream errStream = new Stream();
				errStream.write(searchResponse.getBytes(Constants.XML_CHARACTER_ENCODING));
				errStream.lock();
				return errStream;
			} 
			catch (Exception ex) {
				return null;
			}
			finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (Exception ex) {}
			}
		} finally {
			if (closeRecordStreams) {
				for (Stream s : getRecordStreams()) {
					try {
						s.close();
					} catch (IOException e) {}
				}
			}
		}
	}
		
}
