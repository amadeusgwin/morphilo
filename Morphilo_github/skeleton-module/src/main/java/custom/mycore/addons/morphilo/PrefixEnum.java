/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package custom.mycore.addons.morphilo;

/**
 *
 * @author peukert
 */
public enum PrefixEnum {

    a("a"), ab("ab"), abb("ab"), ac("ac"), ad("ad"), ae("ae"),
    after("after"), ag("ag"), al("al"), ambi("ambi"), amphi("amphi"),
    an("an"), ana("ana"), and("and"), ond("and"),
    ano("ano"), ant("ant"), ante("ante"), anti("anti"), ap("ap"), aph("aph"),
    apo("apo"), ar("ar"), arch("arch"), archi("archi"), as("as"), at("at"),
    avaunt("avaunt"), auaunt("avaunt"), auant("avaunt"), avant("avaunt"),
    awant("avaunt"), be("be"), bes("bes"), bi("bi"), bin("bin"), cata("cata"),
    cat("cata"), cath("cata"), circum("circum"), cis("cis"), citra("citra"),
    co("co"), com("com"), con("con"), contra("contra"), contre("contra"),
    cor("cor"), counter("contra"), de("de"), demi("demi"), des("des"), di("di"),
    dia("dia"), dif("dif"), dis("dis"), dys("dis"), disen("dis"), disem("dis"),
    e("e"), ed("ed"), ef("ef"), em("em"), en("en"), endo("endo"), enter("enter"),
    entre("enter"), ento("ento"), ep("ep"), epana("epana"), epi("epi"), es("es"),
    eso("eso"), ex("ex"), exo("exo"), extra("extra"), extro("extro"), For("for"),
    faer("for"), forr("for"), vor("for"), ver("for"), fur("for"), fore("for"),
    gain("gain"), hemi("hemi"), semi("semi"), semy("semi"), seme("semi"), semie("semie"),
    hyp("hyper"), hyper("hyper"), hypo("hyper"), hytero("hytero"), hyster("hytero"),
    y("y"), i("y"), ge("y"), il("il"), im("im"), in("in"), infra("infra"), inter("inter"),
    intra("intra"), intro("intro"), iodoso("iodoso"), ir("ir"), juxta("juxta"),
    kata("kata"), ker("ker"), ke("ker"), ca("ker"), ka("ker"), che("ker"), male("mal"),
    mal("mal"), meta("meta"), mis("mis"), mys("mis"), miss("mis"), mais("mis"),
    mies("mis"), mise("mis"), mus("mis"), mes("mis"), myse("mis"), myss("mis"),
    misse("mis"), mysse("mis"), misz("mis"), mysz("mis"), miz("mis"), mays("mis"),
    meos("mis"), mess("mis"), messe("mis"), non("non"), nom("non"), nonn("non"),
    nooun("non"), nown("non"), noon("non"), noun("non"), nowne("non"), none("non"),
    nor("nor"), ob("ob"), of("of"), off("of"), af("of"), aff("of"), on("on"), o("on"),
    onn("on"), one("on"), or("or"), ore("or"), orr("or"), out("out"), over("over"),
    ufer("over"), ufor("over"), uferr("over"), uvver("over"), obaer("over"), ober("over)"),
    ofaer("over"), ofere("over"), ofir("over"), ofor("over"), ofer("over"), ouer("over"),
    oferr("over"), offerr("over"), offr("over"), aure("over"), war("over"), euer("over"),
    oferre("over"), oouer("over"), oger("over"), ouere("over"), ouir("over"), ouire("over"),
    ouur("over"), ouver("over"), ouyr("over"), ovar("over"), overe("over"), ovre("over"),
    ovur("over"), owuere("over"), owver("over"), houyr("over"), ouyre("over"), ovir("over"),
    ovyr("over"), hover("over"), auver("over"), awver("over"), ovver("over"), hauver("over"),
    ova("over"), ove("over"), obuh("over"), ovah("over"), ovuh("over"), ofowr("over"),
    ouuer("over"), oure("over"), owere("over"), owr("over"), owre("over"), owur("over"),
    owyr("over"), our("over"), ower("over"), oher("over"), ooer("over"), oor("over"),
    owwer("over"), ovr("over"), owir("over"), oar("over"), aur("over"), oer("over"),
    ufara("over"), ufera("over"), ufere("over"), uferra("over"), ufora("over"), ufore("over"),
    ufra("over"), ufre("over"), ufyrra("over"), yfera("over"), yfere("over"), yferra("over"),
    uuera("over"), ufe("over"), uferre("over"), uuer("over"), uuere("over"), vfere("over"),
    vuer("over"), vuere("over"), vver("over"), uvvor("over"), para("para"), par("para"),
    pene("pene"), paene("pene"), pen("pene"), per("per"), pre("pre"), prae("pre"),
    peri("peri"), post("post"), preter("preter"), praeter("preter"), pro("pro"),
    pur("pur"), re("re"), retro("retro"), sam("sam"), se("se"), self("self"), sous("sous"),
    south("south"), sub("sub"), subter("subter"), Super("super"), supra("supra"), sur("sur"),
    sursum("sursum"), sym("sym"), syn("syn"), to("to"), trans("trans"), tres("tres"),
    ultra("ultra"), um("um"), umb("umb"), umbe("umb"), ummbe("umb"), vmbe("umb"),
    vnbe("umb"), unbe("umb"), wmbe("umb"), ombe("umb"), vnbi("umb"), vmbi("umb"),
    vmby("umb"), unby("umb"), onby("umb"), under("under"), up("up"), ur("ur"),
    vant("vant"), vaunt("vant"), vice("vice"), wan("wan"), with("with"), wither("wither"),
    gae("y"), gi("y"), gie("y"), gy("y"), ie("y"), hi("y");

    private String morpheme;

    //constructor
    PrefixEnum(String morpheme) {
        this.morpheme = morpheme;
    }
    //getter Method

    public String getMorpheme() {
        return this.morpheme;
    }
}
