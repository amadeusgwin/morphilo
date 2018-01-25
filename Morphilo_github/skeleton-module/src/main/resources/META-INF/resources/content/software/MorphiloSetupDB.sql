SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `morphilo` ;
CREATE SCHEMA IF NOT EXISTS `morphilo` DEFAULT CHARACTER SET latin1 ;
USE `morphilo` ;

-- -----------------------------------------------------
-- Table `morphilo`.`head`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`head` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`head` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `head` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`types` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`types` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `type` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`inflmorpheme`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`inflmorpheme` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`inflmorpheme` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `inflection` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`inflection`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`inflection` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`inflection` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `inflMorphemeID` INT(11) NOT NULL ,
  `Inflection` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `inflMorphemeID` (`inflMorphemeID` ASC) ,
  CONSTRAINT `inflMorphemeID`
    FOREIGN KEY (`inflMorphemeID` )
    REFERENCES `morphilo`.`inflmorpheme` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`prefixmorpheme`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`prefixmorpheme` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`prefixmorpheme` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `PrefixMorpheme` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 30
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`prefix`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`prefix` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`prefix` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `PrefixMorphemeID` INT(11) NOT NULL ,
  `Prefix` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `PrefixMorphemID` (`PrefixMorphemeID` ASC) ,
  CONSTRAINT `PrefixMorphemID`
    FOREIGN KEY (`PrefixMorphemeID` )
    REFERENCES `morphilo`.`prefixmorpheme` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 34
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`prefixposition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`prefixposition` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`prefixposition` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `Prefix1ID` INT(11) NOT NULL ,
  `Prefix2ID` INT(11) NOT NULL ,
  `Prefix3ID` INT(11) NOT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `Prefix1ID` (`Prefix1ID` ASC) ,
  INDEX `Prefix2ID` (`Prefix2ID` ASC) ,
  INDEX `Prefix3ID` (`Prefix3ID` ASC) ,
  CONSTRAINT `Prefix1ID`
    FOREIGN KEY (`Prefix1ID` )
    REFERENCES `morphilo`.`prefix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Prefix2ID`
    FOREIGN KEY (`Prefix2ID` )
    REFERENCES `morphilo`.`prefix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Prefix3ID`
    FOREIGN KEY (`Prefix3ID` )
    REFERENCES `morphilo`.`prefix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 45
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`suffixmorpheme`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`suffixmorpheme` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`suffixmorpheme` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `SuffixMorpheme` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 47
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`suffix`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`suffix` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`suffix` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `SuffixMorphemeID` INT(11) NOT NULL ,
  `Suffix` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `SuffixMorphemeID` (`SuffixMorphemeID` ASC) ,
  CONSTRAINT `SuffixMorphemeID`
    FOREIGN KEY (`SuffixMorphemeID` )
    REFERENCES `morphilo`.`suffixmorpheme` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 70
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`suffixposition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`suffixposition` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`suffixposition` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `Suffix1ID` INT(11) NOT NULL ,
  `Suffix2ID` INT(11) NOT NULL ,
  `Suffix3ID` INT(11) NOT NULL ,
  `Suffix4ID` INT(11) NOT NULL ,
  `Suffix5ID` INT(11) NOT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `Suffix1ID` (`Suffix1ID` ASC) ,
  INDEX `Suffix2ID` (`Suffix2ID` ASC) ,
  INDEX `Suffix3ID` (`Suffix3ID` ASC) ,
  INDEX `Suffix4ID` (`Suffix4ID` ASC) ,
  INDEX `Suffix5ID` (`Suffix5ID` ASC) ,
  CONSTRAINT `Suffix1ID`
    FOREIGN KEY (`Suffix1ID` )
    REFERENCES `morphilo`.`suffix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Suffix2ID`
    FOREIGN KEY (`Suffix2ID` )
    REFERENCES `morphilo`.`suffix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Suffix3ID`
    FOREIGN KEY (`Suffix3ID` )
    REFERENCES `morphilo`.`suffix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Suffix4ID`
    FOREIGN KEY (`Suffix4ID` )
    REFERENCES `morphilo`.`suffix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Suffix5ID`
    FOREIGN KEY (`Suffix5ID` )
    REFERENCES `morphilo`.`suffix` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 69
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`word`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`word` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`word` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `SuffixPositionID` INT(11) NOT NULL ,
  `PrefixPositionID` INT(11) NOT NULL ,
  `InflectionID` INT(11) NOT NULL ,
  `Word` VARCHAR(45) NULL DEFAULT NULL ,
  `POS` VARCHAR(5) NULL DEFAULT NULL ,
  `root` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `SuffixPositionID` (`SuffixPositionID` ASC) ,
  INDEX `PrefixPositionID` (`PrefixPositionID` ASC) ,
  INDEX `InflectionID` (`InflectionID` ASC) ,
  CONSTRAINT `InflectionID`
    FOREIGN KEY (`InflectionID` )
    REFERENCES `morphilo`.`inflection` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `PrefixPositionID`
    FOREIGN KEY (`PrefixPositionID` )
    REFERENCES `morphilo`.`prefixposition` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `SuffixPositionID`
    FOREIGN KEY (`SuffixPositionID` )
    REFERENCES `morphilo`.`suffixposition` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1027
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`compound`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`compound` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`compound` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `Word1ID` INT(11) NOT NULL ,
  `Word2ID` INT(11) NOT NULL ,
  `Word3ID` INT(11) NOT NULL ,
  `Word4ID` INT(11) NOT NULL ,
  `Word5ID` INT(11) NOT NULL ,
  `typeID` INT(11) NOT NULL ,
  `headID` INT(11) NOT NULL ,
  `compound` VARCHAR(45) NULL DEFAULT NULL ,
  `pos` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `typeID` (`typeID` ASC) ,
  INDEX `headID` (`headID` ASC) ,
  INDEX `Word1ID` (`Word1ID` ASC) ,
  INDEX `Word2ID` (`Word2ID` ASC) ,
  INDEX `Word3ID` (`Word3ID` ASC) ,
  INDEX `Word4ID` (`Word4ID` ASC) ,
  INDEX `Word5ID` (`Word5ID` ASC) ,
  CONSTRAINT `headID`
    FOREIGN KEY (`headID` )
    REFERENCES `morphilo`.`head` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `typeID`
    FOREIGN KEY (`typeID` )
    REFERENCES `morphilo`.`types` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Word1ID`
    FOREIGN KEY (`Word1ID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Word2ID`
    FOREIGN KEY (`Word2ID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Word3ID`
    FOREIGN KEY (`Word3ID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Word4ID`
    FOREIGN KEY (`Word4ID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Word5ID`
    FOREIGN KEY (`Word5ID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 25
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`corpora`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`corpora` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`corpora` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `CorpusName` VARCHAR(45) NULL DEFAULT NULL ,
  `YearBegin` VARCHAR(45) NULL DEFAULT NULL ,
  `YearEnd` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) )
ENGINE = InnoDB
AUTO_INCREMENT = 79
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`occurrenceCompound`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`occurrenceCompound` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`occurrenceCompound` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `CompoundID` INT(11) NOT NULL ,
  `CorpusID` INT(11) NOT NULL ,
  `Occurrence` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `CorpusID` (`CorpusID` ASC) ,
  INDEX `CompoundID` (`CompoundID` ASC) ,
  CONSTRAINT `CompoundID`
    FOREIGN KEY (`CompoundID` )
    REFERENCES `morphilo`.`compound` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `CorpusID`
    FOREIGN KEY (`CorpusID` )
    REFERENCES `morphilo`.`corpora` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 30
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `morphilo`.`occurrences`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `morphilo`.`occurrences` ;

CREATE  TABLE IF NOT EXISTS `morphilo`.`occurrences` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT ,
  `WordID` INT(11) NOT NULL ,
  `CorporaID` INT(11) NOT NULL ,
  `Occurrence` INT(11) NULL DEFAULT NULL ,
  `Text` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  INDEX `WordID` (`WordID` ASC) ,
  INDEX `CorporaID` (`CorporaID` ASC) ,
  CONSTRAINT `CorporaID`
    FOREIGN KEY (`CorporaID` )
    REFERENCES `morphilo`.`corpora` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `WordID`
    FOREIGN KEY (`WordID` )
    REFERENCES `morphilo`.`word` (`ID` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 1234
DEFAULT CHARACTER SET = latin1;

-- -------------------- --
-- initial entry setup  --
-- -------------------- --

USE morphilo;
INSERT INTO suffixmorpheme(ID,SuffixMorpheme) VALUES(1,"");
INSERT INTO suffix(ID,SuffixMorphemeID,Suffix) VALUES(1,1,"");
INSERT INTO suffixposition(ID,Suffix1ID,Suffix2ID,Suffix3ID,Suffix4ID,Suffix5ID) VALUES(1,1,1,1,1,1);
INSERT INTO prefixmorpheme(ID,PrefixMorpheme) VALUES(1,"");
INSERT INTO prefix(ID,PrefixMorphemeID,Prefix) VALUES(1,1,"");
INSERT INTO prefixposition(ID,Prefix1ID,Prefix2ID,Prefix3ID) VALUES(1,1,1,1);
INSERT INTO inflmorpheme(ID,inflection) VALUES(1,"");
INSERT INTO inflection(ID,inflMorphemeID,Inflection) VALUES(1,1,"");
INSERT INTO word(ID,SuffixPositionID,PrefixPositionID,InflectionID,Word,POS,root) VALUES(1,1,1,1,"","","");


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


