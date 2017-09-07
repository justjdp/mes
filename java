package scgzgl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

/**
 * 
 * 生产订单控制类
 * @author Administrator
 *
 */
public class ScddController extends Controller {
	
	static int g_infNumber = 10;
	static String xqdh = null;
	static String scddh = null;
	static String wlbm = null;
	
	public void index() {
		g_infNumber = 10;
		xqdh = null;
		scddh = null;
		wlbm = null;
		renderFreeMarker("/scgzgl/scdd.html");
	}

	/* 显示整体页面 */
	public void all() {
		if (getPara("wlbm") != null||getPara("xqdh") != null|| getPara("scddh") != null) {
			xqdh = getPara("xqdh");
			scddh = getPara("scddh");
			wlbm = getPara("wlbm");
		}
		if (getParaToInt("infNumber") != null) {
			g_infNumber = getParaToInt("infNumber");
		}
		if (xqdh == null || scddh == null|| wlbm == null) {
			Page<?> scddb = Db.paginate(getParaToInt("pageNumber", 1),
					g_infNumber, "SELECT * ", "FROM scddb ORDER BY kssj asc ");

			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("scddb", scddb);

			List<Object> data = new ArrayList<Object>();
			data.add(map1);
			renderJson(data);
		} else {
			System.out.println("999999999999999999999");
			Page<?> scddb = Db.paginate(getParaToInt("pageNumber", 1),
					g_infNumber, "SELECT * ",
					"FROM scddb where  xqdh like '%" + xqdh
							+ "%' and scddh like '%" + scddh + "%'and wlbm like '%" + wlbm + "%' ORDER BY kssj asc ");
			setAttr("pN", scddb.getPageNumber());
			setAttr("tP", scddb.getTotalPage());
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("scddb", scddb);
			List<Object> data = new ArrayList<Object>();
			data.add(map1);
			renderJson(data);
		}

	}
	public void fanye() {
		g_infNumber = getParaToInt("infNumber");
		renderNull();
	}

	public void scpg(){
		/**
		 * 根据wlbm查工序，生产派工单
		 */
		System.out.println("88888888888888");		
		String idlist = getPara("idlist");
		String ids[] = idlist.split(",");	
		for (int j = 0; j < ids.length; j++) {
			int id = Integer.valueOf(ids[j]);
			Scddb.dao.findById(id).set("zt", "已派工").update();
			Scddb  column1 = Scddb.dao.findByIdLoadColumns(id,"xqdh,scddh,wlbm,wlmc,ggxh,dw,sl,kssj,jssj,ckwd,bz");
			String scddh=column1.getStr("scddh");
			String xqdh=column1.getStr("xqdh");
			String wlbm=column1.getStr("wlbm");
			String wlmc=column1.getStr("wlmc");
			String ggxh=column1.getStr("ggxh");
			String dw=column1.getStr("dw");
			int sl=column1.getInt("sl");
			String ckwd=column1.getStr("ckwd");
			String bz=column1.getStr("bz");
			Date kssj=column1.getDate("kssj");
			List<Gygl> gygl=Gygl.dao.find("select * from gygl where wlbm = '"+ wlbm + "'" );
			int gygl1=gygl.size();
			for(int i = 0; i < gygl1; i++){
				String gxmc=gygl.get(i).getStr("gxmc");
				String gxbm=gygl.get(i).getStr("gxbm");
				List<Gxgl> gxgl=Gxgl.dao.find("select gs from gxgl where gxbm = '"+ gxbm + "'" );
				float gs=gxgl.get(0).get("gs");
				float zgs=gs*sl;
				float days=zgs/60/8;
				int day1=(int)days; 
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				String jssj=df.format((kssj.getTime() + (long)day1 * 24 * 60 * 60 * 1000)); 
				System.out.println(jssj+"+++++++++++++++=");
				Date dates=null;
				try {
					dates = df.parse(jssj);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dates = java.sql.Date.valueOf(jssj);
            	Pgd pgd=new Pgd();
				pgd.set("scddh", scddh).set("xqdh", xqdh).set("wlbm", wlbm).set("wlmc", wlmc)
			      .set("ggxh", ggxh).set("dw", dw).set("pgsl", sl).set("ckwd", ckwd).set("kssj", kssj).set("jssj", dates).set("gs", zgs).set("bz", bz).set("gxmc", gxmc).save();				
			}				
		}
		renderText("success");	
	}
	
}
