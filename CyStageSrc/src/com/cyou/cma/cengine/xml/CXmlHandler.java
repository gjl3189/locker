package com.cyou.cma.cengine.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Rect;

import com.cyou.cma.cengine.CyActor;
import com.cyou.cma.cengine.CyBone;
import com.cyou.cma.cengine.CyStage;
import com.cyou.cma.cengine.CyStageConfig;
import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.cengine.anim.CyBoneAnimation;

public class CXmlHandler extends DefaultHandler{
	private CyStage st = null;
	private StringBuilder bd = null;

//	public static final String ID = "id";
//	public static final String NAME = "name";
	public static final String POSITION = "position";
	public static final String SCALE_TYPE = "scale_type";
	public static final String PIC = "pic";
	public static final String PIC_NAME = "pic_name";
	public static final String MEDIA = "media";
	public static final String MEDIA_NAME = "media_name";
	public static final String WIDTH_HEIGHT = "width_height";
	public static final String SRC_SCALE = "src_scale";
	
	public static final String AC = "actor";
	public static final String AC_NAME = "ac_name";
	public static final String DURATION = "duration";
	public static final String REPEAT = "repeat";
	public static final String INITIATIVE = "is_initiative";
	
	public static final String BN = "bone";
	public static final String RECT_SRC = "rect_src";
	public static final String BA = "ba";
	public static final String IS_PARENT = "is_parent";
	public static final String IS_CHILD = "is_child";
	public static final String IS_NUM = "is_num";
	public static final String IS_MIRROR = "is_mirror";
	public static final String START_END = "start_end";
	public static final String START_END_INVISIBLE = "start_end_invisible";
	public static final String RECT_LTWHXY = "rect_ltwhxy";
	public static final String RECT_SCALE = "rect_scale";
	public static final String RECT_FROM = "rect_from";
	public static final String RECT_TO = "rect_to";
	public static final String RECT_SMFT = "rect_smft";//fromSx,fromSy,toSx,toSy,sX,sY need RECT_LTWHXY
	public static final String RECT_FT = "rect_ft";
	public static final String ROTATE = "rotate";
	public static final String RECT_ROTATE = "rect_rotate";
	public static final String ALPHA = "alpha";
	public static final String PARENT_ID = "parent_id";
	public static final String RATE = "rate";
	public static final String CONSTANT = "constant";
	public static final String ACCELERATE = "accelerate";
	public static final String DECELERATE = "decelerate";
	public static final String[] aryRate = {
		CONSTANT, ACCELERATE, DECELERATE
	};
	
	public static final String MATCH_PARENT = "mx";

	//temp variable
	private float scaleX = 1;
	private float scaleY = 1;
	private int w = 0;
	private int h = 0;
	private int cx = 0;
	private int cy = 0;
	private float sx = 1;
	private float sy = 1;
	private void initBn(){
		w = 0;
		h = 0;
		cx = 0;
		cy = 0;
		sx = scaleX;
		sy = scaleY;
	}
	public CXmlHandler(CyStage st){
		this.st = st;
	}
	//兼容旧版本
	private boolean repeat = false;
	private long duration = 0;
	//end
	@Override
	public void startDocument() throws SAXException{
		super.startDocument();
		bd = new StringBuilder();
		bd.setLength(0);
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atr) throws SAXException{
		super.startElement(uri, localName, qName, atr);
		String tag = localName.length()!=0?localName:qName;
//		android.util.Log.d("____", "tag="+tag);
		if(tag.equals(AC)){
			CyActor ac = new CyActor();
			st.addActor(ac);
			if(repeat){
				ac.repeat = true;
			}
			if(duration>0){
				ac.dr = duration;
			}
//			bd.setLength(0);
		}else if(tag.equals(BN)){
			CyBone bn = new CyBone(new Rect(), st);
			st.getLastActor().addBone(bn);
//			bd.setLength(0);
			initBn();
		}else if(tag.equals(BA)){
			CyBone bn = st.getLastActor().getLastBone();
			CyBoneAnimation ba = new CyBoneAnimation();
			bn.addAnim(ba);
			ba.bh = new CyBaHelper();
			ba.bh.scaleX = scaleX;
			ba.bh.scaleY = scaleY;
			ba.bh.w = w;
			ba.bh.h = h;
			ba.bh.cx = cx;
			ba.bh.cy = cy;
			ba.bh.fromX = cx;
			ba.bh.fromY = cy;
		}
		bd.setLength(0);
	}
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException{
		super.characters(ch, start, length);
		bd.append(ch, start, length);
	}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		super.endElement(uri, localName, qName);
		String tag = localName.length()!=0?localName:qName;
//		android.util.Log.d("____", "tag="+tag);
//		if(tag.equals(ID)){
//			st.id = bd.toString();
//		}else 
		if(tag.equals(PARENT_ID)){
			String id = bd.toString();
			if(id!=null&&id.equals("")){
				id = null;
			}
			st.parentId = id;
		}
//		else if(tag.equals(NAME)){
//			st.name = bd.toString();
//		}
		else if(tag.equals(POSITION)){
			st.p = Byte.parseByte(bd.toString());
		}else if(tag.equals(SCALE_TYPE)){
			st.scaleType = Float.parseFloat(bd.toString());
		}else if(tag.equals(PIC)){
			if(CyStageConfig.isDebug||CyStageConfig.isInAsset(st.config)){
				st.picUrl = bd.toString();
			}
		}else if(tag.equals(MEDIA)){
			if(CyStageConfig.isDebug||CyStageConfig.isInAsset(st.config)){
				st.mediaUrl = bd.toString();
			}
		}else if(tag.equals(PIC_NAME)){
			if(!CyStageConfig.isDebug){
				st.picUrl = bd.toString();
			}
		}else if(tag.equals(MEDIA_NAME)){
			if(!CyStageConfig.isDebug){
				st.mediaUrl = bd.toString();
			}
		}else if(tag.equals(INITIATIVE)){
			CyActor ac = st.getLastActor();
			if(ac!=null){
				ac.isInitiative = Boolean.valueOf(bd.toString()).booleanValue();
				CyTool.log("ac.isInitiative="+ac.isInitiative);
			}
		}else if(tag.equals(AC_NAME)){
			CyActor ac = st.getLastActor();
			if(ac!=null){
				ac.name = bd.toString();
			}
		}else if(tag.equals(REPEAT)){
			CyActor ac = st.getLastActor();
			if(ac!=null){
				ac.repeat = true;
			}else{
				repeat = true;
			}
		}else if(tag.equals(DURATION)){
			long dr = Long.parseLong(bd.toString());
			st.dr = dr;
			CyActor ac = st.getLastActor();
			if(ac!=null){
				ac.dr = dr;
			}else{
				duration = dr;
			}
		}else if(tag.equals(WIDTH_HEIGHT)){
			String[] ary = CyTool.getAry(bd.toString());
			st.sW = Integer.parseInt(ary[0]);
			st.sH = Integer.parseInt(ary[1]);
		}else if(tag.equals(SRC_SCALE)){
			String[] ary = CyTool.getAry(bd.toString());
			scaleX = Float.parseFloat(ary[0]);
			scaleY = Float.parseFloat(ary[1]);
		}else if(tag.equals(RECT_SRC)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			bn.rcBmp.left = Integer.parseInt(ary[0]);
			bn.rcBmp.top = Integer.parseInt(ary[1]);
			bn.rcBmp.right = Integer.parseInt(ary[2]);
			bn.rcBmp.bottom = Integer.parseInt(ary[3]);
		}else if(tag.equals(RECT_LTWHXY)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			bn.rcBmp.left = Integer.parseInt(ary[0]);
			bn.rcBmp.top = Integer.parseInt(ary[1]);
			w = Integer.parseInt(ary[2]);
			bn.rcBmp.right = bn.rcBmp.left + w;
			h = Integer.parseInt(ary[3]);
			bn.rcBmp.bottom = bn.rcBmp.top + h;
			if(ary.length==6){
				cx = Integer.parseInt(ary[4]);
				cy = Integer.parseInt(ary[5]);
			}else{
				cx = w/2;
				cy = h/2;
			}
		}else if(tag.equals(RECT_SCALE)){
			String[] ary = CyTool.getAry(bd.toString());
			sx = scaleX*Float.parseFloat(ary[0]);
			sy = scaleY*Float.parseFloat(ary[1]);
		}else if(tag.equals(ALPHA)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
			ba.setAlpha(Integer.parseInt(ary[0]), Integer.parseInt(ary[1]));
		}else if(tag.equals(IS_MIRROR)){
			CyBone bn = st.getLastActor().getLastBone();
			CyBoneAnimation ba = bn.getLastAnim();
			ba.isMirror = Boolean.parseBoolean(bd.toString());
		}else if(tag.equals(RATE)){
			CyBone bn = st.getLastActor().getLastBone();
			CyBoneAnimation ba = bn.getLastAnim();
			ba.rt = bd.toString();
		}else if(tag.equals(START_END)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
			ba.ary[0] = Float.parseFloat(ary[0]);
			ba.ary[1] = Float.parseFloat(ary[1]);

			ba.bh.cx = cx;
			ba.bh.cy = cy;
			ba.bh.rtFromX = cx;
			ba.bh.rtFromY = cy;
		}else if(tag.equals(START_END_INVISIBLE)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
			ba.ary[0] = Float.parseFloat(ary[0]);
			ba.ary[1] = Float.parseFloat(ary[1]);
			ba.isShow = false;
		}else if(tag.equals(RECT_FROM)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
//			ba.isRectAction = true;
			ba.fromLeft = Integer.parseInt(ary[0]);
			ba.fromTop = Integer.parseInt(ary[1]);
			ba.fromRight = Integer.parseInt(ary[2]);
			ba.fromBottom = Integer.parseInt(ary[3]);
		}else if(tag.equals(RECT_TO)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
			ba.toLeft = Integer.parseInt(ary[0]);
			ba.toTop = Integer.parseInt(ary[1]);
			ba.toRight = Integer.parseInt(ary[2]);
			ba.toBottom = Integer.parseInt(ary[3]);
		}else if(tag.equals(RECT_SMFT)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
			ba.bh.scaleFromX = Float.parseFloat(ary[0]);
			ba.bh.scaleFromY = Float.parseFloat(ary[1]);
			ba.bh.scaleToX = Float.parseFloat(ary[2]);
			ba.bh.scaleToY = Float.parseFloat(ary[3]);
			ba.bh.fromX = Integer.parseInt(ary[4]);
			ba.bh.fromY = Integer.parseInt(ary[5]);
			ba.bh.toX = ba.bh.fromX;
			ba.bh.toY = ba.bh.fromY;
			if(ary.length>=8){
				ba.bh.toX = Integer.parseInt(ary[6]);
				ba.bh.toY = Integer.parseInt(ary[7]);
			}
			if(ary.length>=9){
				if(ary[8].equals(MATCH_PARENT)){
					ba.matchParentX = true;
				};
			}
			setSMFT(ba, sx, sy);
		}else if(tag.equals(RECT_FT)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
//			ba.isRectAction = true;
			int x = Integer.parseInt(ary[0]);
			int y = Integer.parseInt(ary[1]);
			ba.fromLeft = x - (int)(sx*ba.bh.cx);
			ba.fromTop = y - (int)(sy*ba.bh.cy);
			ba.fromRight = x + (int)(sx*(ba.bh.w - ba.bh.cx));
			ba.fromBottom = y + (int)(sy*(ba.bh.h - ba.bh.cy));
			if(ary.length==4){
				x = Integer.parseInt(ary[2]);
				y = Integer.parseInt(ary[3]);
			}
			ba.toLeft = x - (int)(sx*ba.bh.cx);
			ba.toTop = y - (int)(sy*ba.bh.cy);
			ba.toRight = x + (int)(sx*(ba.bh.w - ba.bh.cx));
			ba.toBottom = y + (int)(sy*(ba.bh.h - ba.bh.cy));
		}else if(tag.equals(ROTATE)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
//			ba.isRotate = true;
			ba.fromR = Float.parseFloat(ary[0]);
			ba.toR = Float.parseFloat(ary[1]);
			ba.offsetX = Integer.parseInt(ary[2]);
			ba.offsetY = Integer.parseInt(ary[3]);
		}else if(tag.equals(RECT_ROTATE)){
			CyBone bn = st.getLastActor().getLastBone();
			String[] ary = CyTool.getAry(bd.toString());
			CyBoneAnimation ba = bn.getLastAnim();
//			ba.isRotate = true;
			ba.fromR = Float.parseFloat(ary[0]);
			ba.toR = Float.parseFloat(ary[1]);
			ba.bh.rtFromX = Integer.parseInt(ary[2]);
			ba.bh.rtFromY = Integer.parseInt(ary[3]);
			setRT(ba);
		}else if(tag.equals(IS_PARENT)){
			CyActor ac = st.getLastActor();
			CyBone bn = ac.getLastBone();
			ac.setParent(bn);
			st.loadActor(ac);
			bn.setParent(true);
			bn.setChild(false);
		}else if(tag.equals(IS_CHILD)){
			CyBone bn = st.getLastActor().getLastBone();
			bn.setParent(false);
			bn.setChild(true);
		}else if(tag.equals(IS_NUM)){
			CyBone bn = st.getLastActor().getLastBone();
			bn.drawSelf = false;
		}
	}
	public static void setRT(CyBoneAnimation ba){
		ba.offsetX = (int)(ba.bh.rtFromX*ba.bh.scaleX);
		ba.offsetY = (int)(ba.bh.rtFromY*ba.bh.scaleY);
	}
	public static void setSMFT(CyBoneAnimation ba){
		setSMFT(ba, ba.bh.scaleX, ba.bh.scaleY);
	}
	public static void setSMFT(CyBoneAnimation ba, float sx, float sy){
//		ba.isRectAction = true;
		ba.fromLeft = ba.bh.fromX - (int)(ba.bh.scaleFromX*sx*ba.bh.cx);
		ba.fromTop = ba.bh.fromY - (int)(ba.bh.scaleFromY*sy*ba.bh.cy);
		ba.fromRight = ba.bh.fromX + (int)(ba.bh.scaleFromX*sx*(ba.bh.w - ba.bh.cx));
		ba.fromBottom = ba.bh.fromY + (int)(ba.bh.scaleFromY*sy*(ba.bh.h - ba.bh.cy));
		ba.toLeft = ba.bh.toX - (int)(ba.bh.scaleToX*sx*ba.bh.cx);
		ba.toTop = ba.bh.toY - (int)(ba.bh.scaleToY*sy*ba.bh.cy);
		ba.toRight = ba.bh.toX + (int)(ba.bh.scaleToX*sx*(ba.bh.w - ba.bh.cx));
		ba.toBottom = ba.bh.toY + (int)(ba.bh.scaleToY*sy*(ba.bh.h - ba.bh.cy));
	}
}
