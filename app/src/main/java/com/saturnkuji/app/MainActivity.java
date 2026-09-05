package com.saturnkuji.app;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static class Prize { int number; String grade,name,image; boolean drawn; Prize(int n,String g,String name){number=n;grade=g;this.name=name;} }
    final ArrayList<Prize> prizes=new ArrayList<>(); ArrayList<Prize> lastDraw=new ArrayList<>();
    SharedPreferences pref; FrameLayout frame; LinearLayout root; TextView count,grades,capsule,result,undo;
    boolean drawing=false,canUndo=false; Prize selectedPhoto; final int GOLD=Color.rgb(255,210,74);
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);if(bold)t.setTypeface(null,1);return t;}
    GradientDrawable bg(String c,int r){GradientDrawable g=new GradientDrawable();g.setColor(Color.parseColor(c));g.setCornerRadius(dp(r));g.setStroke(dp(1),0x55666666);return g;}
    GradientDrawable rarityBg(String grade,int r){GradientDrawable g=new GradientDrawable();g.setColor(0xff21192d);g.setCornerRadius(dp(r));g.setStroke(dp(2),rarityColor(grade));return g;}
    Button btn(String s,String c,View.OnClickListener l){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(16);b.setAllCaps(false);b.setBackground(bg(c,14));b.setOnClickListener(v->{tap(v);l.onClick(v);});return b;}
    LinearLayout.LayoutParams lp(int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(top);return p;}
    LinearLayout.LayoutParams half(int left){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(58),1f);p.leftMargin=dp(left);return p;}
    void tap(View v){v.animate().scaleX(.96f).scaleY(.96f).setDuration(60).withEndAction(()->v.animate().scaleX(1).scaleY(1).setDuration(90).start()).start();sound(R.raw.click);vibrate(20);}

    @Override public void onCreate(Bundle b){super.onCreate(b);pref=getSharedPreferences("saturn",0);load();showSplash();}
    void showSplash(){FrameLayout s=new FrameLayout(this);s.setBackground(gradient("#07040D","#24102F"));LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);TextView logo=tv("🪐",82,GOLD,true);TextView name=tv("SATURN KUJI",32,Color.WHITE,true);TextView sub=tv("V9 • LEGENDARY CAPSULE EXPERIENCE",12,0xffc9bdd4,true);box.addView(logo);box.addView(name);box.addView(sub);s.addView(box,new FrameLayout.LayoutParams(-1,-1,Gravity.CENTER));setContentView(s);box.setScaleX(.7f);box.setScaleY(.7f);box.setAlpha(0f);box.animate().alpha(1).scaleX(1).scaleY(1).setDuration(700).setInterpolator(new OvershootInterpolator(1.1f)).withEndAction(()->new Handler().postDelayed(()->buildHome(),420)).start();}
    void base(String active){frame=new FrameLayout(this);ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(15),dp(18),dp(15),dp(100));root.setBackground(gradient("#090611","#24102F"));sc.addView(root);frame.addView(sc,new FrameLayout.LayoutParams(-1,-1));frame.addView(bottomNav(active),new FrameLayout.LayoutParams(-1,dp(76),Gravity.BOTTOM));setContentView(frame);}
    LinearLayout bottomNav(String active){LinearLayout nav=new LinearLayout(this);nav.setGravity(Gravity.CENTER);nav.setPadding(dp(6),dp(5),dp(6),dp(5));nav.setBackground(bg("#17131F",0));String[] n={"⌂\nHome","🎰\nDraw","▣\nCollection","⚙\nAdmin"};for(String x:n){String key=x.contains("Home")?"Home":x.contains("Draw")?"Draw":x.contains("Collection")?"Collection":"Admin";boolean a=key.equals(active);TextView t=tv(x,12,a?GOLD:0xffd0cad8,true);t.setGravity(Gravity.CENTER);t.setBackground(bg(a?"#2A2035":"#17131F",10));View.OnClickListener l=key.equals("Home")?v->buildHome():key.equals("Draw")?v->drawTab():key.equals("Collection")?v->inventory():v->admin();t.setOnClickListener(v->{tap(v);l.onClick(v);});nav.addView(t,new LinearLayout.LayoutParams(0,-1,1));}return nav;}
    LinearLayout box(String c,int r){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setGravity(Gravity.CENTER);l.setPadding(dp(10),dp(10),dp(10),dp(10));l.setBackground(bg(c,r));return l;}
    void buildHome(){base("Home");root.addView(tv("🪐 SATURN KUJI",31,GOLD,true));root.addView(tv("행운을 뽑아라! • V9 LEGENDARY",15,Color.WHITE,false));LinearLayout banner=box("#24162F",18);banner.addView(tv("⚡ LEGENDARY KUJI ⚡",20,GOLD,true));count=tv("",22,Color.WHITE,true);grades=tv("",13,0xffd8d0df,false);banner.addView(count);banner.addView(grades);root.addView(banner,lp(12));TextView hero=tv("🎰  캡슐을 열고 상품을 확인하세요  🎰",16,GOLD,true);hero.setPadding(dp(8),dp(12),dp(8),dp(12));hero.setBackground(bg("#332244",16));root.addView(hero,lp(12));capsule=tv("🔴",94,Color.WHITE,false);root.addView(capsule,lp(8));result=tv("DRAW 탭에서 뽑기를 시작하세요!",17,Color.WHITE,true);result.setPadding(dp(8),dp(14),dp(8),dp(14));result.setBackground(bg("#251C31",16));root.addView(result,lp(4));root.addView(tv("🎰 뽑기 선택",20,GOLD,true),lp(16));LinearLayout r1=new LinearLayout(this);r1.addView(btn("1회 뽑기","#C73D19",v->draw(1)),half(0));r1.addView(btn("3회 연속","#B91D25",v->draw(3)),half(8));root.addView(r1);LinearLayout r2=new LinearLayout(this);r2.addView(btn("5회 연속","#66289B",v->draw(5)),half(0));r2.addView(btn("10회 연속","#45206F",v->draw(10)),half(8));root.addView(r2,lp(8));undo=btn("↩ 뽑기 취소 · 직전 상태 복구","#343434",v->undo());root.addView(undo,lp(8));LinearLayout quick=new LinearLayout(this);quick.addView(btn("🔢 상품목록","#244A83",v->numbers()),half(0));quick.addView(btn("📦 내 보관함","#463260",v->inventory()),half(8));root.addView(quick,lp(16));root.addView(tv("✨ V9: 암전 → 금빛 플래시 → 캡슐 파편 → 상품 확대 → S LEGENDARY 홀로그램",12,0xffaaa3b7,false),lp(14));update();}
    void drawTab(){base("Draw");root.addView(tv("🎰 DRAW",30,GOLD,true));root.addView(tv("캡슐을 터치하고 행운을 확인하세요",14,0xffd8d0df,false));capsule=tv("🔴",116,Color.WHITE,false);root.addView(capsule,lp(20));TextView hint=tv("CAPSULE READY\n아래 버튼을 눌러 뽑기 시작",16,Color.WHITE,true);hint.setPadding(8,16,8,16);hint.setBackground(bg("#251C31",18));root.addView(hint,lp(6));LinearLayout r1=new LinearLayout(this);r1.addView(btn("1회","#C73D19",v->draw(1)),half(0));r1.addView(btn("3회","#B91D25",v->draw(3)),half(8));root.addView(r1,lp(18));LinearLayout r2=new LinearLayout(this);r2.addView(btn("5회","#66289B",v->draw(5)),half(0));r2.addView(btn("10회","#45206F",v->draw(10)),half(8));root.addView(r2,lp(8));undo=btn("↩ 직전 뽑기 취소","#343434",v->undo());root.addView(undo,lp(8));undo.setEnabled(canUndo);update();}

    void draw(int n){if(drawing)return;ArrayList<Prize> av=available();if(av.isEmpty()){toast("남은 쿠지가 없습니다.");return;}drawing=true;canUndo=false;if(undo!=null)undo.setEnabled(false);lastDraw.clear();if(capsule!=null)capsule.setText("🔴");sound(R.raw.click);vibrate(35);AnimatorSet set=new AnimatorSet();set.playTogether(ObjectAnimator.ofFloat(capsule,View.ROTATION,0,720,1440),ObjectAnimator.ofFloat(capsule,View.SCALE_X,1,1.2f,.82f,1.25f,1),ObjectAnimator.ofFloat(capsule,View.SCALE_Y,1,1.2f,.82f,1.25f,1));set.setDuration(1550);set.setInterpolator(new AccelerateDecelerateInterpolator());set.addListener(new AnimatorListenerAdapter(){public void onAnimationEnd(Animator a){capsule.setRotation(0);pop();stagePulse();finishDraw(n);drawing=false;}});set.start();new CountDownTimer(1400,240){public void onTick(long x){sound(R.raw.drum);if(x<500)vibrate(18);}public void onFinish(){}}.start();}
    ArrayList<Prize> available(){ArrayList<Prize>a=new ArrayList<>();for(Prize p:prizes)if(!p.drawn)a.add(p);return a;}
    void finishDraw(int n){ArrayList<Prize> av=available();Collections.shuffle(av);int take=Math.min(n,av.size());for(int i=0;i<take;i++){av.get(i).drawn=true;lastDraw.add(av.get(i));}Prize best=bestPrize();if(best.grade.equals("S상")){capsule.setText("💥");sound(R.raw.rare);vibratePattern(new long[]{0,140,70,140,70,260});flash(GOLD);}else if(best.grade.equals("A상")){capsule.setText("✨");sound(R.raw.win);vibratePattern(new long[]{0,90,60,140});flash(0xffb76cff);}else{capsule.setText("🎁");sound(R.raw.win);vibrate(60);}if(result!=null){StringBuilder s=new StringBuilder("🎉 당첨 결과!");for(Prize p:lastDraw)s.append("\n").append(p.grade).append(" · ").append(p.name).append(" (#").append(String.format("%03d",p.number)).append(")");result.setText(s.toString());}canUndo=true;if(undo!=null)undo.setEnabled(true);save();update();showResultPopup(best);}
    Prize bestPrize(){Prize b=lastDraw.get(0);for(Prize p:lastDraw)if(rank(p.grade)<rank(b.grade))b=p;return b;}

    // V8 HERO RESULT SEQUENCE: capsule split -> light breakthrough -> 3D product card -> particles
    // V9 S-LEGENDARY HERO: full blackout -> gold flash -> capsule shards toward camera -> product zoom -> hologram
    void showResultPopup(Prize p){
        final Dialog d=new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        final FrameLayout stage=new FrameLayout(this);
        stage.setBackgroundColor(Color.BLACK);
        stage.setClipChildren(false);

        final View blackout=new View(this);
        blackout.setBackgroundColor(Color.BLACK);
        stage.addView(blackout,new FrameLayout.LayoutParams(-1,-1));

        final V9HeroView hero=new V9HeroView(this,rarityColor(p.grade),p.grade.equals("S상"));
        stage.addView(hero,new FrameLayout.LayoutParams(-1,-1));

        final ProductCard card=new ProductCard(this,p);
        stage.addView(card,new FrameLayout.LayoutParams(-1,dp(520),Gravity.CENTER));
        card.setAlpha(0f); card.setScaleX(.32f); card.setScaleY(.32f);
        card.setRotationY(-18f); card.setTranslationY(dp(70));
        card.setElevation(dp(18));
        card.setVisibility(View.INVISIBLE);

        final HologramView holo=new HologramView(this);
        stage.addView(holo,new FrameLayout.LayoutParams(-1,-1));
        holo.setAlpha(0f); holo.setVisibility(View.INVISIBLE);

        Button close=btn("확인 · 보관함에 저장","#7A4AA8",v->d.dismiss());
        FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(dp(250),dp(54),Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        cp.bottomMargin=dp(12); stage.addView(close,cp); close.setAlpha(0f);

        d.setContentView(stage);
        d.setCancelable(true);
        d.show();
        Window w=d.getWindow();
        if(w!=null){
            w.setBackgroundDrawableResource(android.R.color.transparent);
            w.setLayout(-1,-1);
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if(p.grade.equals("S상")){
            // 0ms: total blackout
            hero.startBlackout();
            stage.postDelayed(()->{
                hero.goldFlash();
                sound(R.raw.rare);
                vibratePattern(new long[]{0,100,45,150,45,300});
            },180);
            // 260ms: capsule splits and shards fly toward the viewer
            stage.postDelayed(()->hero.shatter(),360);
            stage.postDelayed(()->{
                card.setVisibility(View.VISIBLE);
                hero.hideCapsule();
                card.animate().alpha(1f).scaleX(1.13f).scaleY(1.13f).rotationY(0f).translationY(dp(-10))
                    .setDuration(430).setInterpolator(new DecelerateInterpolator(1.4f))
                    .withEndAction(()->{
                        card.animate().scaleX(1f).scaleY(1f).translationY(0).setDuration(260).start();
                        holo.setVisibility(View.VISIBLE);
                        holo.setAlpha(1f);
                        holo.start();
                        close.animate().alpha(1f).setDuration(260).start();
                    }).start();
                card.launchParticles(stage,"S상");
                rarityPulse(card,"S상");
                sound(R.raw.rare);
            },860);
        }else{
            // Non-S results retain a fast premium result reveal.
            hero.setVisibility(View.GONE);
            stage.setBackgroundColor(0xf2090611);
            card.setVisibility(View.VISIBLE);
            card.animate().alpha(1).scaleX(1).scaleY(1).rotationY(0).translationY(0)
                .setDuration(650).setInterpolator(new OvershootInterpolator(1.05f)).start();
            card.launchParticles(stage,p.grade);
            rarityPulse(card,p.grade);
            sound(R.raw.win); vibrate(55);
            close.animate().alpha(1f).setDuration(250).setStartDelay(500).start();
        }
    }

    // V9 full-screen cinematic layer. S only.
    class V9HeroView extends View{
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        int color; boolean legendary;
        float blackout=1f, flash=0f, shard=0f, rot=0f; boolean hidden=false;
        ArrayList<Shard> shards=new ArrayList<>();
        Random rnd=new Random();
        class Shard{float x,y,vx,vy,scale,spin,size;Shard(){x=getWidth()/2f+(rnd.nextFloat()-.5f)*150;y=getHeight()/2f+(rnd.nextFloat()-.5f)*180;vx=(rnd.nextFloat()-.5f)*1800;vy=(rnd.nextFloat()-.5f)*1800;scale=.35f+rnd.nextFloat()*.65f;spin=(rnd.nextFloat()-.5f)*8;size=dp(18+rnd.nextInt(24));}}
        V9HeroView(Context c,int color,boolean legendary){super(c);this.color=color;this.legendary=legendary;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void startBlackout(){blackout=1;invalidate(); ValueAnimator a=ValueAnimator.ofFloat(1f,0.18f);a.setDuration(170);a.addUpdateListener(v->{blackout=(Float)v.getAnimatedValue();invalidate();});a.start();}
        void goldFlash(){ValueAnimator a=ValueAnimator.ofFloat(0,1,.0f);a.setDuration(330);a.addUpdateListener(v->{flash=(Float)v.getAnimatedValue();invalidate();});a.start();}
        void shatter(){
            shards.clear(); for(int i=0;i<(legendary?30:16);i++)shards.add(new Shard());
            ValueAnimator a=ValueAnimator.ofFloat(0,1);a.setDuration(560);a.setInterpolator(new AccelerateInterpolator(1.8f));
            a.addUpdateListener(v->{shard=(Float)v.getAnimatedValue();rot+=.08f;invalidate();});a.start();
        }
        void hideCapsule(){hidden=true;invalidate();}
        protected void onDraw(Canvas c){
            super.onDraw(c); float cx=getWidth()/2f,cy=getHeight()/2f;
            if(!hidden && shard<.72f){
                p.setStyle(Paint.Style.FILL);p.setColor(0xffd52f3c);
                c.drawCircle(cx,cy,dp(82),p);
                p.setColor(0xffff6870);c.drawCircle(cx+dp(34),cy-dp(5),dp(48),p);
                p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(5));p.setColor(0xffffeeee);c.drawCircle(cx,cy,dp(82),p);
                p.setStyle(Paint.Style.FILL);p.setColor(color);
                c.drawCircle(cx,cy,dp(17)+dp(35)*shard,p);
            }
            // gold flash fills the entire display
            if(flash>0){
                p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(255,214,80));
                p.setAlpha((int)(235*flash));c.drawRect(0,0,getWidth(),getHeight(),p);p.setAlpha(255);
                p.setShader(new RadialGradient(cx,cy,Math.max(getWidth(),getHeight())*.72f,0xffffffff,0x00ffd54a,Shader.TileMode.CLAMP));
                p.setAlpha((int)(180*flash));c.drawRect(0,0,getWidth(),getHeight(),p);p.setShader(null);p.setAlpha(255);
            }
            if(shard>0){
                for(Shard q:shards){
                    float x=q.x+q.vx*shard, y=q.y+q.vy*shard;
                    float sc=q.scale*(.7f+2.8f*shard);
                    p.setStyle(Paint.Style.FILL);p.setColor(0xffdf3340);p.setAlpha((int)(255*(1-.45f*shard)));
                    c.save();c.rotate(q.spin*shard*57.3f,x,y);c.scale(sc,sc,x,y);
                    Path path=new Path();path.moveTo(x-q.size,y-q.size*.55f);path.lineTo(x+q.size*.7f,y-q.size*.3f);path.lineTo(x+q.size*.4f,y+q.size);path.lineTo(x-q.size*.8f,y+q.size*.55f);path.close();c.drawPath(path,p);
                    p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2));p.setColor(0xffffd86b);c.drawPath(path,p);c.restore();
                }
                p.setAlpha(255);
            }
            if(blackout>0){
                p.setStyle(Paint.Style.FILL);p.setColor(Color.BLACK);p.setAlpha((int)(245*blackout));c.drawRect(0,0,getWidth(),getHeight(),p);p.setAlpha(255);
            }
        }
    }

    class HologramView extends View{
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); float t=0;
        HologramView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void start(){ValueAnimator a=ValueAnimator.ofFloat(0,1);a.setDuration(1900);a.setRepeatCount(ValueAnimator.INFINITE);a.addUpdateListener(v->{t=(Float)v.getAnimatedValue();invalidate();});a.start();}
        protected void onDraw(Canvas c){
            float cx=getWidth()/2f, top=getHeight()/2f-dp(250), bottom=top+dp(520);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2));p.setColor(0xffffd84f);p.setAlpha(90);
            for(int i=0;i<14;i++){float y=top+dp(40)+i*dp(34)+dp(7)*(float)Math.sin((t*6+i)*1.4);c.drawLine(dp(18),y,getWidth()-dp(18),y,p);}
            p.setStrokeWidth(dp(3));p.setAlpha(175);
            RectF r=new RectF(dp(8),top,getWidth()-dp(8),bottom);c.drawRoundRect(r,dp(26),dp(26),p);
            p.setStyle(Paint.Style.FILL);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            p.setTextAlign(Paint.Align.CENTER);p.setTextSize(dp(34));p.setShadowLayer(dp(18),0,0,0xffffd84f);p.setColor(0xfffff0a1);p.setAlpha(210);
            c.drawText("S  LEGENDARY",cx,top+dp(58),p);
            p.clearShadowLayer();
            p.setTextSize(dp(12));p.setAlpha(155);c.drawText("★ HOLOGRAPHIC PRIZE ★",cx,top+dp(82),p);
            // moving rainbow-ish hologram bands using alpha only, preserving gold premium palette
            p.setTextSize(dp(18));p.setAlpha(125);
            c.drawText("LIMITED • ULTRA RARE • S",cx,top+dp(110),p);
            p.setAlpha(255);
        }
    }

    class ProductCard extends LinearLayout{
        Prize prize; ImageView image; TextView title;
        ProductCard(Context c,Prize p){super(c);prize=p;setOrientation(VERTICAL);setGravity(Gravity.CENTER);setPadding(dp(18),dp(18),dp(18),dp(70));setBackground(rarityBg(p.grade,26));setCameraDistance(dp(9000));TextView top=tv(p.grade.equals("S상")?"★ LEGENDARY ★":p.grade.equals("A상")?"✦ EPIC ✦":"✦ RESULT ✦",14,rarityColor(p.grade),true);addView(top,new LinearLayout.LayoutParams(-1,dp(30)));image=new ImageView(c);image.setScaleType(ImageView.ScaleType.CENTER_CROP);image.setBackground(bg("#160F20",18));if(p.image!=null){try{image.setImageURI(Uri.parse(p.image));}catch(Exception e){image.setImageResource(android.R.drawable.ic_menu_gallery);}}else image.setImageResource(android.R.drawable.ic_menu_gallery);addView(image,new LinearLayout.LayoutParams(-1,dp(265)));title=tv(p.name,25,Color.WHITE,true);title.setPadding(0,dp(12),0,0);addView(title,new LinearLayout.LayoutParams(-1,dp(60)));TextView no=tv("KUJI NO. #"+String.format("%03d",p.number),14,0xffc9c1d1,false);addView(no,new LinearLayout.LayoutParams(-1,dp(32)));if(lastDraw.size()>1)addView(tv("+ 추가 당첨 "+(lastDraw.size()-1)+"개 · 보관함에서 확인",13,0xffaaa3b7,false),new LinearLayout.LayoutParams(-1,dp(30)));}
        void launchParticles(FrameLayout stage,String grade){ParticleView pv=new ParticleView(MainActivity.this,rarityColor(grade),grade.equals("S상"));stage.addView(pv,new FrameLayout.LayoutParams(-1,-1));pv.start();stage.postDelayed(()->stage.removeView(pv),1500);}
    }

    static class CapsuleBreakView extends View{
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); int color; boolean legendary; float spin=0,split=0,beam=0; ValueAnimator a;
        CapsuleBreakView(Context c,int color,boolean legendary){super(c);this.color=color;this.legendary=legendary;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void start(){a=ValueAnimator.ofFloat(0,1);a.setDuration(700);a.setInterpolator(new AccelerateDecelerateInterpolator());a.addUpdateListener(v->{spin=(Float)v.getAnimatedValue();invalidate();});a.start();}
        void split(){ValueAnimator x=ValueAnimator.ofFloat(0,1);x.setDuration(430);x.setInterpolator(new OvershootInterpolator(1.3f));x.addUpdateListener(v->{split=(Float)v.getAnimatedValue();invalidate();});x.start();}
        void beam(){ValueAnimator x=ValueAnimator.ofFloat(0,1);x.setDuration(520);x.setInterpolator(new DecelerateInterpolator());x.addUpdateListener(v->{beam=(Float)v.getAnimatedValue();invalidate();});x.start();}
        protected void onDraw(Canvas c){super.onDraw(c);float cx=getWidth()/2f,cy=getHeight()/2f-20;float scale=getResources().getDisplayMetrics().density;float r=72*scale;float gap=70*scale*split; p.setStyle(Paint.Style.FILL);p.setShadowLayer(18*scale,0,0,color);p.setColor(0xffd62b35);RectF left=new RectF(cx-r-gap,cy-r,cx-gap,cy+r);c.drawArc(left,90,180,true,p);p.setColor(0xffff5b63);RectF right=new RectF(cx+gap,cy-r,cx+r+gap,cy+r);c.drawArc(right,-90,180,true,p);p.clearShadowLayer();p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(7*scale);p.setColor(0xfff5d6d6);c.drawOval(new RectF(cx-r-gap,cy-r,cx-gap,cy+r),p);c.drawOval(new RectF(cx+gap,cy-r,cx+r+gap,cy+r),p);
            if(beam>0){p.setStyle(Paint.Style.FILL);p.setShader(new LinearGradient(cx,cy+50*scale,cx,cy-210*scale,0x00ffffff,0xccffffff,Shader.TileMode.CLAMP));Path path=new Path();float bw=(55+170*beam)*scale;path.moveTo(cx-bw,cy+60*scale);path.lineTo(cx+bw,cy+60*scale);path.lineTo(cx+bw*.48f,cy-250*scale);path.lineTo(cx-bw*.48f,cy-250*scale);path.close();c.drawPath(path,p);p.setShader(null);p.setColor(color);p.setAlpha((int)(220*beam));c.drawCircle(cx,cy-10*scale,18*scale+30*scale*beam,p);p.setAlpha(255);}
            if(split<.98f){p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3*scale);for(int i=0;i<(legendary?18:10);i++){double ang=i*Math.PI*2/(legendary?18:10)+spin*6.28;float rr=(float)((92+8*Math.sin(i))*scale);float x=cx+(float)Math.cos(ang)*rr,y=cy+(float)Math.sin(ang)*rr;c.drawLine(x,y,x+(float)Math.cos(ang)*22*scale,y+(float)Math.sin(ang)*22*scale,p);}}
        }
    }

    static class ParticleView extends View{
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); ArrayList<Pt> pts=new ArrayList<>(); int color;boolean gold;float progress;
        static class Pt{float a,r,v,size;Pt(float a,float r,float v,float size){this.a=a;this.r=r;this.v=v;this.size=size;}}
        ParticleView(Context c,int color,boolean gold){super(c);this.color=color;this.gold=gold;Random rnd=new Random();for(int i=0;i<(gold?70:34);i++)pts.add(new Pt(rnd.nextFloat()*6.283f,20+rnd.nextFloat()*80,90+rnd.nextFloat()*230,2+rnd.nextFloat()*5));setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void start(){ValueAnimator a=ValueAnimator.ofFloat(0,1);a.setDuration(1250);a.setInterpolator(new DecelerateInterpolator());a.addUpdateListener(v->{progress=(Float)v.getAnimatedValue();invalidate();});a.start();}
        protected void onDraw(Canvas c){float cx=getWidth()/2f,cy=getHeight()/2f;p.setStyle(Paint.Style.FILL);for(Pt q:pts){float rr=(q.r+q.v*progress)*getResources().getDisplayMetrics().density;float x=cx+(float)Math.cos(q.a)*rr;float y=cy+(float)Math.sin(q.a)*rr;p.setColor(gold && q.size%2>1?0xffffe38a:color);p.setAlpha((int)(255*(1-progress)));c.drawCircle(x,y,q.size*(1-progress/2),p);} }
    }

    int rarityColor(String g){return g.equals("S상")?0xffffd24a:g.equals("A상")?0xffc77dff:g.equals("B상")?0xff4da6ff:g.equals("C상")?0xff66d6a8:0xffc5c9d1;}
    void rarityPulse(View v,String g){int c=rarityColor(g);v.setTranslationZ(dp(4));ValueAnimator a=ValueAnimator.ofFloat(1f,.82f,1f);a.setDuration(g.equals("S상")?720:1000);a.setRepeatCount(g.equals("S상")?4:2);a.addUpdateListener(x->{float f=(Float)x.getAnimatedValue();v.setAlpha(f);});a.start();}
    void stagePulse(){if(frame==null)return;frame.animate().translationX(dp(5)).setDuration(45).withEndAction(()->frame.animate().translationX(dp(-5)).setDuration(45).withEndAction(()->frame.animate().translationX(0).setDuration(70).start()).start());}
    void pop(){AnimatorSet s=new AnimatorSet();s.playTogether(ObjectAnimator.ofFloat(capsule,View.SCALE_X,.65f,1.5f,1),ObjectAnimator.ofFloat(capsule,View.SCALE_Y,.65f,1.5f,1));s.setDuration(520);s.start();}
    int rank(String g){return g.equals("S상")?0:g.equals("A상")?1:g.equals("B상")?2:g.equals("C상")?3:4;}
    void undo(){if(!canUndo||lastDraw.size()==0){toast("취소할 뽑기가 없습니다.");return;}for(Prize p:lastDraw)p.drawn=false;lastDraw.clear();canUndo=false;if(undo!=null)undo.setEnabled(false);if(capsule!=null)capsule.setText("↩️");if(result!=null)result.setText("직전 뽑기를 취소했습니다.\n번호와 수량이 뽑기 전 상태로 복구되었습니다.");sound(R.raw.cancel);vibrate(70);save();update();}
    void inventory(){base("Collection");root.addView(tv("📦 내 보관함",28,GOLD,true));root.addView(tv("당첨된 상품을 카드로 확인하세요",14,0xffd8d0df,false));ArrayList<Prize> list=new ArrayList<>();for(Prize p:prizes)if(p.drawn)list.add(p);if(list.isEmpty())root.addView(tv("아직 당첨된 상품이 없습니다.",17,Color.WHITE,false),lp(30));for(Prize p:list)root.addView(prizeCard(p,true),lp(10));update();}
    View prizeCard(Prize p,boolean showNumber){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.HORIZONTAL);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(dp(10),dp(10),dp(10),dp(10));card.setBackground(rarityBg(p.grade,16));ImageView img=new ImageView(this);img.setScaleType(ImageView.ScaleType.CENTER_CROP);img.setLayoutParams(new LinearLayout.LayoutParams(dp(76),dp(76)));if(p.image!=null){try{img.setImageURI(Uri.parse(p.image));}catch(Exception e){img.setImageResource(android.R.drawable.ic_menu_gallery);}}else img.setImageResource(android.R.drawable.ic_menu_gallery);card.addView(img);TextView info=tv(p.grade+"\n"+p.name+(showNumber?"\n번호 #"+String.format("%03d",p.number):""),16,Color.WHITE,true);info.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);info.setPadding(dp(14),0,0,0);card.addView(info,new LinearLayout.LayoutParams(0,-2,1));return card;}
    void numbers(){base("Home");root.addView(tv("🔢 001~100 상품 목록",27,GOLD,true));root.addView(tv("번호별 남은 상품과 당첨 상태",14,0xffd8d0df,false));ArrayList<Prize> list=new ArrayList<>(prizes);Collections.sort(list,(a,b)->a.number-b.number);for(Prize p:list){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(10),dp(8),dp(10),dp(8));row.setBackground(bg(p.drawn?"#3A2638":"#202638",12));TextView n=tv("#"+String.format("%03d",p.number),15,GOLD,true);n.setLayoutParams(new LinearLayout.LayoutParams(dp(60),-2));row.addView(n);TextView state=tv(p.drawn?"🎁 뽑힘":"⬜ 남음",14,Color.WHITE,false);state.setGravity(Gravity.LEFT);row.addView(state,new LinearLayout.LayoutParams(dp(85),-2));TextView info=tv(p.grade+" · "+p.name,14,Color.WHITE,false);info.setGravity(Gravity.LEFT);row.addView(info,new LinearLayout.LayoutParams(0,-2,1));root.addView(row,lp(5));}}
    void admin(){final EditText e=new EditText(this);e.setHint("관리자 비밀번호");e.setInputType(0x81);new AlertDialog.Builder(this).setTitle("⚙ 관리자 모드").setMessage("기본 비밀번호: 351284").setView(e).setNegativeButton("취소",null).setPositiveButton("입장",(d,w)->{String pass=pref.getString("pass","351284");if(e.getText().toString().equals(pass))adminPanel();else toast("비밀번호가 틀렸습니다.");}).show();}
    void adminPanel(){base("Admin");root.addView(tv("⚙ 세턴쿠지 관리자",27,GOLD,true));root.addView(tv("상품 이미지 · 상품명 · 등급 · 번호를 관리합니다",14,0xffd8d0df,false));root.addView(btn("🔀 번호와 상품 랜덤 섞기","#244A83",v->{for(Prize p:prizes)if(p.drawn){toast("전체 리셋 후 섞어주세요.");return;}Collections.shuffle(prizes);for(int i=0;i<prizes.size();i++)prizes.get(i).number=i+1;save();toast("상품을 랜덤 배치했습니다.");}),lp(12));root.addView(btn("🔄 전체 리셋","#236B36",v->reset()),lp(8));root.addView(btn("🔐 비밀번호 변경","#5D3A16",v->changePass()),lp(8));root.addView(tv("상품 카드 편집",18,GOLD,true),lp(16));ArrayList<Prize> list=new ArrayList<>(prizes);Collections.sort(list,(a,b)->a.number-b.number);for(Prize p:list){View card=prizeCard(p,true);card.setOnClickListener(v->edit(p));root.addView(card,lp(8));}}
    void edit(Prize p){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(8),0,dp(8),0);EditText name=new EditText(this);name.setText(p.name);Spinner sp=new Spinner(this);String[] gs={"S상","A상","B상","C상","D상"};sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,gs));sp.setSelection(rank(p.grade));Button photo=btn(p.image==null?"🖼 상품 사진 등록":"🖼 상품 사진 변경","#4A3158",v->{selectedPhoto=p;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,99);});l.addView(name);l.addView(sp);l.addView(photo,lp(6));new AlertDialog.Builder(this).setTitle("#"+String.format("%03d",p.number)+" 상품 편집").setView(l).setNegativeButton("취소",null).setPositiveButton("저장",(d,w)->{p.name=name.getText().toString();p.grade=gs[sp.getSelectedItemPosition()];save();toast("저장되었습니다.");}).show();}
    @Override protected void onActivityResult(int r,int c,Intent data){super.onActivityResult(r,c,data);if(r==99&&c==RESULT_OK&&data!=null&&data.getData()!=null&&selectedPhoto!=null){selectedPhoto.image=data.getData().toString();try{getContentResolver().takePersistableUriPermission(data.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}save();toast("상품 사진이 등록되었습니다.");}}
    void changePass(){EditText e=new EditText(this);e.setHint("새 비밀번호 (4자리 이상)");e.setInputType(0x81);new AlertDialog.Builder(this).setTitle("관리자 비밀번호 변경").setView(e).setNegativeButton("취소",null).setPositiveButton("저장",(d,w)->{if(e.getText().length()<4)toast("4자리 이상 입력하세요.");else{pref.edit().putString("pass",e.getText().toString()).apply();toast("비밀번호 변경 완료");}}).show();}
    void reset(){new AlertDialog.Builder(this).setTitle("전체 리셋").setMessage("100개 쿠지를 새로 랜덤 배치할까요?").setNegativeButton("취소",null).setPositiveButton("리셋",(d,w)->{for(Prize p:prizes)p.drawn=false;Collections.shuffle(prizes);for(int i=0;i<prizes.size();i++)prizes.get(i).number=i+1;lastDraw.clear();canUndo=false;if(undo!=null)undo.setEnabled(false);save();if(capsule!=null)capsule.setText("🔴");if(result!=null)result.setText("🔄 전체 쿠지가 초기화되었습니다!");sound(R.raw.win);vibratePattern(new long[]{0,60,50,60});update();});}
    void update(){if(count==null||grades==null)return;int left=0;for(Prize p:prizes)if(!p.drawn)left++;count.setText("남은 쿠지 "+left+" / "+prizes.size());StringBuilder s=new StringBuilder();for(String x:new String[]{"S상","A상","B상","C상","D상"}){int n=0;for(Prize p:prizes)if(!p.drawn&&p.grade.equals(x))n++;if(s.length()>0)s.append("   ");s.append(x).append(' ').append(n);}grades.setText(s);}
    void save(){StringBuilder sb=new StringBuilder();for(Prize p:prizes){if(sb.length()>0)sb.append("|||");sb.append(p.number).append("::").append(p.grade).append("::").append(p.name.replace("|","/")).append("::").append(p.image==null?"":p.image).append("::").append(p.drawn);}pref.edit().putString("data",sb.toString()).apply();}
    void load(){String data=pref.getString("data","");if(data.isEmpty()){int n=1;add(n++,"S상","세턴 레전드 피규어");for(int i=0;i<5;i++)add(n++,"A상","한정판 피규어");for(int i=0;i<10;i++)add(n++,"B상","프리미엄 아크릴 스탠드");for(int i=0;i<30;i++)add(n++,"C상","캐릭터 굿즈");for(int i=0;i<54;i++)add(n++,"D상","랜덤 키링");Collections.shuffle(prizes);for(int i=0;i<prizes.size();i++)prizes.get(i).number=i+1;save();return;}for(String row:data.split("\\|\\|\\|")){String[] a=row.split("::",-1);if(a.length>=5){try{Prize p=new Prize(Integer.parseInt(a[0]),a[1],a[2]);p.image=a[3].isEmpty()?null:a[3];p.drawn=Boolean.parseBoolean(a[4]);prizes.add(p);}catch(Exception ignored){}}}}
    void add(int n,String g,String name){prizes.add(new Prize(n,g,name));}
    void sound(int id){try{MediaPlayer mp=MediaPlayer.create(this,id);if(mp!=null){mp.setOnCompletionListener(x->x.release());mp.start();}}catch(Exception ignored){}}
    void vibrate(long ms){Vibrator v=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(ms,VibrationEffect.DEFAULT_AMPLITUDE));else v.vibrate(ms);}
    void vibratePattern(long[] p){Vibrator v=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createWaveform(p,-1));else v.vibrate(p,-1);}
    void flash(int color){if(root==null)return;root.setBackgroundColor(color);root.animate().alpha(.65f).setDuration(90).withEndAction(()->{root.setBackground(gradient("#090611","#24102F"));root.animate().alpha(1f).setDuration(230).start();}).start();}
    GradientDrawable gradient(String a,String b){return new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.parseColor(a),Color.parseColor(b)});}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
