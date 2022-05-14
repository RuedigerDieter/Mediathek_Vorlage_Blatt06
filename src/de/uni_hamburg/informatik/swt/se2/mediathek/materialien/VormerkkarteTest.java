package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Kundennummer;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.CD;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;

public class VormerkkarteTest
{
    private Datum _datum;
    private Vormerkkarte _karte;
    private Kunde _kunde;
    private Medium _medium;

    public VormerkkarteTest()
    {
        _kunde = new Kunde(new Kundennummer(123456), "ich", "du");

        _datum = Datum.heute();

        _medium = new CD("bar", "baz", "foo", 123);
        _karte = new Vormerkkarte(_kunde, _medium, _datum);
    }

    @Test
    public void testegetFormatiertenString() throws Exception
    {
        assertNotNull(_karte.getFormatiertenString());
    }

    @Test
    public void testeKonstruktor() throws Exception
    {
        assertEquals(_kunde, _karte.getVormerker());
        assertEquals(_medium, _karte.getMedium());
        assertEquals(_datum, _karte.getVormerkdatum());
    }

    @Test
    public void testgetVormerkdauer()
    {
        Datum vormerkdatum = Datum.heute().minus(10);
        int dauer = 11;
        _karte = new Vormerkkarte(_kunde, _medium, vormerkdatum);
        assertEquals(_karte.getVormerkdauer(), dauer);
    }
    
    @Test
    public void testgetVormerkdatum()
    {
        assertEquals(_karte.getVormerkdatum(), _datum);
    }
    
    @Test
    public void testgetVormerker()
    {
        assertEquals(_karte.getVormerker(), _kunde);
    }
    
    @Test
    public void testgetMedium()
    {
        assertEquals(_karte.getMedium(), _medium);
    }
    
    @Test
    public void testEquals()
    {
        Vormerkkarte karte1 = new Vormerkkarte(_kunde, _medium, _datum);

        assertTrue(_karte.equals(karte1));
        assertEquals(_karte.hashCode(), karte1.hashCode());

        Kunde kunde2 = new Kunde(new Kundennummer(654321), "ich", "du");
        CD medium2 = new CD("hallo", "welt", "foo", 321);
        Datum datum2 = Datum.heute()
            .minus(1);
        Vormerkkarte karte2 = new Vormerkkarte(kunde2, medium2, datum2);

        assertFalse(_karte.equals(karte2));
        assertNotSame(_karte.hashCode(), karte2.hashCode());

    }
}
