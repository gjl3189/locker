package com.cyou.cma.cengine.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.cyou.cma.cengine.CyStage;

public class CyStageXmlHelper {
	private static CyStageXmlHelper xh = new CyStageXmlHelper();
	public synchronized static CyStageXmlHelper getInstance() {
        return xh;
    }
	public boolean parse(InputStream is, CyStage st, Context ct){
		try {
			SAXParserFactory sf = SAXParserFactory.newInstance();
			SAXParser sp = sf.newSAXParser();
			CXmlHandler hd = new CXmlHandler(st);
			sp.parse(is, hd);
			return true;
		} catch (ParserConfigurationException e) {
			showToast(e.toString(), ct);
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			showToast(e.toString(), ct);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			showToast(e.toString(), ct);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			showToast(e.toString(), ct);
			e.printStackTrace();
			return false;
		}
	}
	private void showToast(final String str, final Context ct){
		try{
			Activity at = (Activity)ct;
			at.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ct, str, Toast.LENGTH_LONG).show();
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
