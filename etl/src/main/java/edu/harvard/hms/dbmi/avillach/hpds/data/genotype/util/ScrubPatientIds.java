package edu.harvard.hms.dbmi.avillach.hpds.data.genotype.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.google.common.cache.LoadingCache;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import edu.harvard.hms.dbmi.avillach.hpds.data.genotype.VariantStore;
import edu.harvard.hms.dbmi.avillach.hpds.data.phenotype.ColumnMeta;
import edu.harvard.hms.dbmi.avillach.hpds.data.phenotype.PhenoCube;

public class ScrubPatientIds {
	protected static LoadingCache<String, PhenoCube<?>> store;

	protected static TreeMap<String, ColumnMeta> metaStoreSource;

	protected static TreeSet<Integer> allIds;

	private static final int PATIENT_NUM = 0;

	private static final int CONCEPT_PATH = 1;

	private static final int NUMERIC_VALUE = 2;

	private static final int TEXT_VALUE = 3;
	
	private static final Logger log = Logger.getLogger(ScrubPatientIds.class);
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException {
		
		ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream("/opt/local/hpds/all/variantStore.javabin")));
		VariantStore variantStore = (VariantStore) objectInputStream.readObject();
		objectInputStream.close();
		
		String[] oldPatientIds = variantStore.getPatientIds();
		String[] newPatientIds = new String[oldPatientIds.length];
		
		CsvReader reader = new CsvReader();
		CsvContainer csv = reader.read(new FileReader("/opt/local/hpds/patientIdsToKeep.csv"));
		HashSet<String> idsToKeep = new HashSet<String>();
		for(CsvRow row : csv.getRows()) {
			idsToKeep.add(row.getField(0).trim());
		}
		for(int x = 0;x<oldPatientIds.length;x++) {
			if(idsToKeep.contains(oldPatientIds[x].trim())) {
				newPatientIds[x]=oldPatientIds[x].trim();
				log.info("Keeping  : " + oldPatientIds[x]);
			}else {
				newPatientIds[x] = "-1";
				log.info("Scrubbing  :  " + oldPatientIds[x]);
			}
		}
		variantStore.setPatientIds(newPatientIds);
		
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("/opt/local/hpds/all/variantStoreRemapped.javabin")));
		out.writeObject(variantStore);
		out.flush();out.close();
	}
	
}
