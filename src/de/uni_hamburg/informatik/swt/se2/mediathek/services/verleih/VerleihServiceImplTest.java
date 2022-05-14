package de.uni_hamburg.informatik.swt.se2.mediathek.services.verleih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Kundennummer;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Verleihkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Vormerkkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.CD;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.ServiceObserver;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.kundenstamm.KundenstammService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.kundenstamm.KundenstammServiceImpl;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.medienbestand.MedienbestandService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.medienbestand.MedienbestandServiceImpl;

/**
 * @author SE2-Team
 */
public class VerleihServiceImplTest
{
    private Datum _datum;
    private Kunde _kunde;
    private VerleihService _service;
    private List<Medium> _medienListe;
    private Kunde _vormerkkunde;
    private Kunde _vormerkkunde2;
    private Kunde _vormerkkunde3;
    private Kunde _vormerkkunde4;

    public VerleihServiceImplTest()
    {
        _datum = new Datum(3, 4, 2009);
        KundenstammService kundenstamm = new KundenstammServiceImpl(
                new ArrayList<Kunde>());
        _kunde = new Kunde(new Kundennummer(123456), "ich", "du");

        _vormerkkunde = new Kunde(new Kundennummer(666999), "paul", "panter");
        _vormerkkunde2 = new Kunde(new Kundennummer(666998), "paula", "panter");
        _vormerkkunde3 = new Kunde(new Kundennummer(666997), "pauli", "panter");
        _vormerkkunde4 = new Kunde(new Kundennummer(666996), "paule", "panter");

        kundenstamm.fuegeKundenEin(_kunde);
        kundenstamm.fuegeKundenEin(_vormerkkunde);
        kundenstamm.fuegeKundenEin(_vormerkkunde2);
        kundenstamm.fuegeKundenEin(_vormerkkunde3);
        kundenstamm.fuegeKundenEin(_vormerkkunde4);
        MedienbestandService medienbestand = new MedienbestandServiceImpl(
                new ArrayList<Medium>());
        Medium medium = new CD("CD1", "baz", "foo", 123);
        medienbestand.fuegeMediumEin(medium);
        medium = new CD("CD2", "baz", "foo", 123);
        medienbestand.fuegeMediumEin(medium);
        medium = new CD("CD3", "baz", "foo", 123);
        medienbestand.fuegeMediumEin(medium);
        medium = new CD("CD4", "baz", "foo", 123);
        medienbestand.fuegeMediumEin(medium);
        _medienListe = medienbestand.getMedien();
        _service = new VerleihServiceImpl(kundenstamm, medienbestand,
                new ArrayList<Verleihkarte>());
    }

    @Test
    public void testeNachInitialisierungIstNichtsVerliehenOderVorgemerkt() throws Exception
    {
        assertTrue(_service.getVerleihkarten()
            .isEmpty());
        assertFalse(_service.istVerliehen(_medienListe.get(0)));
        assertFalse(_service.sindAlleVerliehen(_medienListe));
        assertTrue(_service.sindAlleNichtVerliehen(_medienListe));
        assertTrue(_service.getAlleVormerkkarten().isEmpty());
        assertFalse(_service.istVorgemerkt(_medienListe.get(0)));
        assertFalse(_service.sindAlleVorgemerkt(_medienListe));
        assertTrue(_service.sindAlleNichtVorgemerkt(_medienListe));
    }

    @Test
    public void testeVormerkenUndLoeschenVonVormerkung() throws ProtokollierException
    {
        //Legt eine Liste mit vorgemerkten und eine mit nicht vorgemerkten Medien an.
        List<Medium> vorgemerkteMedien = _medienListe.subList(0, 2);
        List<Medium> nichtVorgemerkteMedien = _medienListe.subList(2, 4);
        _service.merkeVor(_vormerkkunde, vorgemerkteMedien, _datum);
        
        // Prüfe, ob alle sondierenden Operationen für das Vertragsmodell
        // funktionieren
        assertTrue(_service.istVorgemerkt(vorgemerkteMedien.get(0)));
        assertTrue(_service.istVorgemerkt(vorgemerkteMedien.get(1)));
        assertFalse(_service.istVorgemerkt(nichtVorgemerkteMedien.get(0)));
        assertFalse(_service.istVorgemerkt(nichtVorgemerkteMedien.get(1)));
        assertTrue(_service.sindAlleVorgemerkt(vorgemerkteMedien));
        assertTrue(_service.sindAlleNichtVorgemerkt(nichtVorgemerkteMedien));
        assertFalse(_service.sindAlleNichtVorgemerkt(vorgemerkteMedien));
        assertFalse(_service.sindAlleVorgemerkt(nichtVorgemerkteMedien));
        assertFalse(_service.sindAlleVorgemerkt(_medienListe));
        assertFalse(_service.sindAlleNichtVorgemerkt(_medienListe));
        assertTrue(_service.istVorgemerktVon(_vormerkkunde, vorgemerkteMedien.get(0)));
        assertTrue(_service.istVorgemerktVon(_vormerkkunde, vorgemerkteMedien.get(1)));
        assertFalse(
                _service.istVorgemerktVon(_vormerkkunde, nichtVorgemerkteMedien.get(0)));
        assertFalse(
                _service.istVorgemerktVon(_vormerkkunde, nichtVorgemerkteMedien.get(1)));
        assertTrue(_service.sindAlleVorgemerktVon(_vormerkkunde, vorgemerkteMedien));
        assertFalse(
                _service.sindAlleVorgemerktVon(_vormerkkunde, nichtVorgemerkteMedien));
        assertTrue(_service.istVormerkenMoeglich(_vormerkkunde2, vorgemerkteMedien));
        assertTrue(_service.istVormerkenMoeglich(_vormerkkunde2, nichtVorgemerkteMedien));
        assertFalse(_service.istVormerkenMoeglich(_vormerkkunde, vorgemerkteMedien));
        assertFalse(_service.istVormerkenMoeglich(_vormerkkunde, _medienListe));
        assertTrue(_service.istVormerkenMoeglich(_vormerkkunde, nichtVorgemerkteMedien));
        
        _service.merkeVor(_vormerkkunde2, vorgemerkteMedien, _datum);
        _service.merkeVor(_vormerkkunde3, vorgemerkteMedien, _datum);
        
        // Prüft, ob bei drei bestehenden Vormerkungen für ein Medium noch weitere Kunden
        // Vormerkungen anstellen können.
        
        assertFalse(_service.istVormerkenMoeglich(_vormerkkunde4, vorgemerkteMedien));
            

        // Prüfe alle sonstigen sondierenden Methoden
        assertEquals(6, _service.getAlleVormerkkarten().size());
        
        _service.loescheVormerkkarten(vorgemerkteMedien, _datum, _vormerkkunde);
        _service.loescheVormerkkarten(vorgemerkteMedien, _datum, _vormerkkunde2);
        _service.loescheVormerkkarten(vorgemerkteMedien, _datum, _vormerkkunde3);
        
        // Prüfe, ob das Löschen der Karten funktioniert
        assertFalse(_service.istVorgemerkt(vorgemerkteMedien.get(0)));
        assertFalse(_service.istVorgemerkt(vorgemerkteMedien.get(1)));
        assertFalse(_service.istVorgemerkt(nichtVorgemerkteMedien.get(0)));
        assertFalse(_service.istVorgemerkt(nichtVorgemerkteMedien.get(1)));
        assertFalse(_service.sindAlleVorgemerkt(vorgemerkteMedien));
        assertTrue(_service.sindAlleNichtVorgemerkt(nichtVorgemerkteMedien));
        assertTrue(_service.sindAlleNichtVorgemerkt(vorgemerkteMedien));
        assertFalse(_service.sindAlleVorgemerkt(nichtVorgemerkteMedien));
        assertFalse(_service.sindAlleVorgemerkt(_medienListe));
        assertTrue(_service.sindAlleNichtVorgemerkt(_medienListe));
        assertTrue(_service.getAlleVormerkkarten().isEmpty());
        assertFalse(_service.sindAlleVorgemerktVon(_vormerkkunde, _medienListe));
        assertFalse(_service.sindAlleVorgemerktVon(_vormerkkunde, vorgemerkteMedien));
    }
    
    @Test
    public void testeVormerkenUndAusleihenVonMedien() throws ProtokollierException
    {
        Datum datum2 = new Datum(4, 4, 2009);
        Datum datum3 = new Datum(5, 4, 2009);
        Vormerkkarte vormerkkarte = new Vormerkkarte(_vormerkkunde, _medienListe.get(0), _datum);
        
        //Kunde darf Medium, dass er entliehen hat, nicht vormerken
        _service.verleiheAn(_kunde, _medienListe, _datum);
        assertFalse(_service.istVormerkenMoeglich(_kunde, _medienListe));
        assertTrue(_service.istVormerkenMoeglich(_vormerkkunde, _medienListe));
        
        _service.nimmZurueck(_medienListe, _datum);
        assertTrue(_service.istVormerkenMoeglich(_kunde, _medienListe));
        assertTrue(_service.istVormerkenMoeglich(_vormerkkunde, _medienListe));
        
        _service.merkeVor(_vormerkkunde, _medienListe, _datum);
        assertEquals(_service.getVormerkkarteFuerKundeUndMedium(_vormerkkunde, _medienListe.get(0)), vormerkkarte);
        //TODO nächsten drei Zeilen werfen immer Assertion Errors: reicht Vertragsmodell ohne JUnit Test?
//        _service.verleiheAn(_kunde, _medienListe, _datum);
//        //wenn der Kunde nicht der Vormerker ist, darf Medium nicht entliehen werden
//        assertFalse(_service.sindAlleVerliehen(_medienListe));
        
        _service.verleiheAn(_vormerkkunde, _medienListe, _datum);
        assertTrue(_service.sindAlleVerliehen(_medienListe));
        //Vormerkungen sollten nach Ausleihe gelöscht werden
        assertFalse(_service.sindAlleVorgemerkt(_medienListe));
        
        _service.nimmZurueck(_medienListe, _datum);
        _service.merkeVor(_vormerkkunde, _medienListe, _datum);
        _service.merkeVor(_vormerkkunde2, _medienListe, datum2);
        _service.merkeVor(_vormerkkunde3, _medienListe, datum3);
        assertTrue(_service.istErstvormerker(_vormerkkunde, _medienListe.get(0)));
        assertTrue(_service.istFuerAlleErstvormerker(_vormerkkunde, _medienListe));
        assertFalse(_service.istErstvormerker(_vormerkkunde2, _medienListe.get(0)));
        assertFalse(_service.istFuerAlleErstvormerker(_vormerkkunde2, _medienListe));
        assertEquals(_service.getVormerkkartenFuer(_medienListe.get(0)).size(), 3);
        assertEquals(_service.getAlleVormerkkarten().size(), (_medienListe.size() * 
                _service.getVormerkkartenFuer(_medienListe.get(0)).size()));
        //nur der Erst-Vormerker sollte Medium ausleihen können
        // TODO Assertion Error: _service.verleiheAn(_vormerkkunde2, _medienListe, _datum);
        assertFalse(_service.sindAlleVerliehen(_medienListe));
        _service.verleiheAn(_vormerkkunde, _medienListe, _datum);
        assertTrue(_service.sindAlleVerliehen(_medienListe));
        //Vormerkungen anderer Kunden sollten noch vorhanden sein
        assertTrue(_service.sindAlleVorgemerkt(_medienListe));
        assertTrue(_service.istErstvormerker(_vormerkkunde2, _medienListe.get(0)));
        assertEquals(_service.getVorgemerkteMedienFuer(_vormerkkunde).size(), 0);
        assertEquals(_service.getVorgemerkteMedienFuer(_vormerkkunde2).size(), _medienListe.size());
        assertEquals(_service.getVormerkkartenFuer(_medienListe.get(0)).size(), 2);
        assertEquals(_service.getAlleVormerkkarten().size(), (_medienListe.size() * 
                _service.getVormerkkartenFuer(_medienListe.get(0)).size()));
        assertEquals(_service.getVorgemerkteMedienFuer(_vormerkkunde2), _service.getVorgemerkteMedienFuer(_vormerkkunde3));
    }
    
    @Test
    public void testeVerleihUndRueckgabeVonMedien() throws Exception
    {
        // Lege eine Liste mit nur verliehenen und eine Liste mit ausschließlich
        // nicht verliehenen Medien an
        List<Medium> verlieheneMedien = _medienListe.subList(0, 2);
        List<Medium> nichtVerlieheneMedien = _medienListe.subList(2, 4);
        _service.verleiheAn(_kunde, verlieheneMedien, _datum);

        // Prüfe, ob alle sondierenden Operationen für das Vertragsmodell
        // funktionieren
        assertTrue(_service.istVerliehen(verlieheneMedien.get(0)));
        assertTrue(_service.istVerliehen(verlieheneMedien.get(1)));
        assertFalse(_service.istVerliehen(nichtVerlieheneMedien.get(0)));
        assertFalse(_service.istVerliehen(nichtVerlieheneMedien.get(1)));
        assertTrue(_service.sindAlleVerliehen(verlieheneMedien));
        assertTrue(_service.sindAlleNichtVerliehen(nichtVerlieheneMedien));
        assertFalse(_service.sindAlleNichtVerliehen(verlieheneMedien));
        assertFalse(_service.sindAlleVerliehen(nichtVerlieheneMedien));
        assertFalse(_service.sindAlleVerliehen(_medienListe));
        assertFalse(_service.sindAlleNichtVerliehen(_medienListe));
        assertTrue(_service.istVerliehenAn(_kunde, verlieheneMedien.get(0)));
        assertTrue(_service.istVerliehenAn(_kunde, verlieheneMedien.get(1)));
        assertFalse(
                _service.istVerliehenAn(_kunde, nichtVerlieheneMedien.get(0)));
        assertFalse(
                _service.istVerliehenAn(_kunde, nichtVerlieheneMedien.get(1)));
        assertTrue(_service.sindAlleVerliehenAn(_kunde, verlieheneMedien));
        assertFalse(
                _service.sindAlleVerliehenAn(_kunde, nichtVerlieheneMedien));

        // Prüfe alle sonstigen sondierenden Methoden
        assertEquals(2, _service.getVerleihkarten()
            .size());

        _service.nimmZurueck(verlieheneMedien, _datum);
        // Prüfe, ob alle sondierenden Operationen für das Vertragsmodell
        // funktionieren
        assertFalse(_service.istVerliehen(verlieheneMedien.get(0)));
        assertFalse(_service.istVerliehen(verlieheneMedien.get(1)));
        assertFalse(_service.istVerliehen(nichtVerlieheneMedien.get(0)));
        assertFalse(_service.istVerliehen(nichtVerlieheneMedien.get(1)));
        assertFalse(_service.sindAlleVerliehen(verlieheneMedien));
        assertTrue(_service.sindAlleNichtVerliehen(nichtVerlieheneMedien));
        assertTrue(_service.sindAlleNichtVerliehen(verlieheneMedien));
        assertFalse(_service.sindAlleVerliehen(nichtVerlieheneMedien));
        assertFalse(_service.sindAlleVerliehen(_medienListe));
        assertTrue(_service.sindAlleNichtVerliehen(_medienListe));
        assertTrue(_service.getVerleihkarten()
            .isEmpty());
    }

    //TODO muss noch für Vormerkung erweitert werden
    @Test
    public void testVerleihEreignisBeobachter() throws ProtokollierException
    {
        final boolean ereignisse[] = new boolean[1];
        ereignisse[0] = false;
        ServiceObserver beobachter = new ServiceObserver()
        {
            @Override
            public void reagiereAufAenderung()
            {
                ereignisse[0] = true;
            }
        };
        _service.verleiheAn(_kunde,
                Collections.singletonList(_medienListe.get(0)), _datum);
        assertFalse(ereignisse[0]);

        _service.registriereBeobachter(beobachter);
        _service.verleiheAn(_kunde,
                Collections.singletonList(_medienListe.get(1)), _datum);
        assertTrue(ereignisse[0]);

        _service.entferneBeobachter(beobachter);
        ereignisse[0] = false;
        _service.verleiheAn(_kunde,
                Collections.singletonList(_medienListe.get(2)), _datum);
        assertFalse(ereignisse[0]);
    }

}
