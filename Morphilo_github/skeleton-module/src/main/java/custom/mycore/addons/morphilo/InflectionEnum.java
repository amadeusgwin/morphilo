package custom.mycore.addons.morphilo;

public enum InflectionEnum 
{	
	ies("Plural"), es("Plural"), en("Plural"), s("Possessiv"),
	inde("Progressive"), ende("Progressive"), and("Progressive"), ing("Progressive"), 
	eth("ThirdPerson"), est("Superlativ"), er("Komparativ"),
	ed("PastTense");
	
	private String inflection;
	
    //constructor
    InflectionEnum(String inflection) 
    {
        this.inflection = inflection;
    }
    
    //getter Method
    public String getInflection() 
    {
        return this.inflection;
    }

}
