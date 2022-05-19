package de.uni_hamburg.informatik.swt.se2.mediathek.services.verleih;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Verleihkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Vormerkkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.AbstractObservableService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.kundenstamm.KundenstammService;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.medienbestand.MedienbestandService;

/**
 * Diese Klasse implementiert das Interface VerleihService. Siehe dortiger
 * Kommentar.
 * 
 * @author SE2-Team
 * @version SoSe 2021
 */
public class VerleihServiceImpl extends AbstractObservableService
        implements VerleihService
{
    /**
     * Diese Map speichert für jedes eingefügte Medium die dazugehörige
     * Verleihkarte. Ein Zugriff auf die Verleihkarte ist dadurch leicht über
     * die Angabe des Mediums möglich. Beispiel: _verleihkarten.get(medium)
     */
    private Map<Medium, Verleihkarte> _verleihkarten;

    /**
     * Map, die alle Vormerkkarten einspeichert. Damit ist der Zugriff auf aktuellste Verleihkarte einfach möglich.
     */
    private Map<Medium, List<Vormerkkarte>> _vormerkkarten;
    /**
     * Der Medienbestand.
     */
    private MedienbestandService _medienbestand;

    /**
     * Der Kundenstamm.
     */
    private KundenstammService _kundenstamm;

    /**
     * Der Protokollierer für die Verleihvorgänge.
     */
    private VerleihProtokollierer _protokollierer;

    /**
     * Konstruktor. Erzeugt einen neuen VerleihServiceImpl.
     * 
     * @param kundenstamm Der KundenstammService.
     * @param medienbestand Der MedienbestandService.
     * @param initialBestand Der initiale Bestand.
     * 
     * @require kundenstamm != null
     * @require medienbestand != null
     * @require initialBestand != null
     */
    public VerleihServiceImpl(KundenstammService kundenstamm,
            MedienbestandService medienbestand,
            List<Verleihkarte> initialBestand)
    {
        assert kundenstamm != null : "Vorbedingung verletzt: kundenstamm  != null";
        assert medienbestand != null : "Vorbedingung verletzt: medienbestand  != null";
        assert initialBestand != null : "Vorbedingung verletzt: initialBestand  != null";
        _verleihkarten = erzeugeVerleihkartenBestand(initialBestand);
        _kundenstamm = kundenstamm;
        _medienbestand = medienbestand;
        //initialisiere leere Map mit Key-Value Paar für jedes Medium im Bestand
        _vormerkkarten = erzeugeVormerkkartenBestand();
        _protokollierer = new VerleihProtokollierer();
    }

    /**
     * Erzeugt eine Hashmap an Vormerkkarten aus dem Initialbestand
     */
    private HashMap<Medium,List<Vormerkkarte>> erzeugeVormerkkartenBestand()
    {
        HashMap<Medium,List<Vormerkkarte>> vormerkkarten = new HashMap<Medium,List<Vormerkkarte>>();
        for(Medium medium : medienbestand.getMedien())
        {
            vormerkkarten.put(medium, new ArrayList<Vormerkkarte>());
        }
        return vormerkkarten;
    }

    {

    }
    /**
     * Erzeugt eine neue HashMap aus dem Initialbestand.
     */
    private HashMap<Medium, Verleihkarte> erzeugeVerleihkartenBestand(
            List<Verleihkarte> initialBestand)
    {
        HashMap<Medium, Verleihkarte> result = new HashMap<Medium, Verleihkarte>();
        for (Verleihkarte verleihkarte : initialBestand)
        {
            result.put(verleihkarte.getMedium(), verleihkarte);
        }
        return result;
    }

    @Override
    public List<Verleihkarte> getVerleihkarten()
    {
        return new ArrayList<Verleihkarte>(_verleihkarten.values());
    }

    @Override
    public boolean istVerliehen(Medium medium)
    {
        assert mediumImBestand(
                medium) : "Vorbedingung verletzt: mediumExistiert(medium)";
        return _verleihkarten.get(medium) != null;
    }

    @Override
    public boolean istVerleihenMoeglich(Kunde kunde, List<Medium> medien)
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert medienImBestand(
                medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        
        return sindAlleNichtVerliehen(medien) && (sindAlleNichtVorgemerkt(medien) 
                || istFuerAlleErstvormerker(kunde, medien));

        //return sindAlleNichtVerliehen(medien);
    }

    @Override
    public void nimmZurueck(List<Medium> medien, Datum rueckgabeDatum)
            throws ProtokollierException
    {
        assert sindAlleVerliehen(
                medien) : "Vorbedingung verletzt: sindAlleVerliehen(medien)";
        assert rueckgabeDatum != null : "Vorbedingung verletzt: rueckgabeDatum != null";

        for (Medium medium : medien)
        {
            Verleihkarte verleihkarte = _verleihkarten.get(medium);
            _verleihkarten.remove(medium);
            _protokollierer.protokolliere(
                    VerleihProtokollierer.EREIGNIS_RUECKGABE, verleihkarte);
        }

        informiereUeberAenderung();
    }

    @Override
    public boolean sindAlleNichtVerliehen(List<Medium> medien)
    {
        assert medienImBestand(
                medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        boolean result = true;
        for (Medium medium : medien)
        {
            if (istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean sindAlleVerliehenAn(Kunde kunde, List<Medium> medien)
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert medienImBestand(
                medien) : "Vorbedingung verletzt: medienImBestand(medien)";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVerliehenAn(kunde, medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean istVerliehenAn(Kunde kunde, Medium medium)
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert mediumImBestand(
                medium) : "Vorbedingung verletzt: mediumImBestand(medium)";

        return istVerliehen(medium) && getEntleiherFuer(medium).equals(kunde);
    }

    @Override
    public boolean sindAlleVerliehen(List<Medium> medien)
    {
        assert medienImBestand(
                medien) : "Vorbedingung verletzt: medienImBestand(medien)";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public void verleiheAn(Kunde kunde, List<Medium> medien, Datum ausleihDatum)
            throws ProtokollierException
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert sindAlleNichtVerliehen(
                medien) : "Vorbedingung verletzt: sindAlleNichtVerliehen(medien) ";
        assert ausleihDatum != null : "Vorbedingung verletzt: ausleihDatum != null";
        assert istVerleihenMoeglich(kunde,
                medien) : "Vorbedingung verletzt:  istVerleihenMoeglich(kunde, medien)";
//        assert (sindAlleNichtVorgemerkt(medien) || istFuerAlleErstvormerker(kunde, medien))  : 
//            "Vorbedingung verletzt: (sindAlleNichtVorgemerkt(medien) || istFuerAlleErstvormerker(kunde, medien))";
        
        for (Medium medium : medien)
        {
            Verleihkarte verleihkarte = new Verleihkarte(kunde, medium,
                    ausleihDatum);

            _verleihkarten.put(medium, verleihkarte);
            _protokollierer.protokolliere(
                    VerleihProtokollierer.EREIGNIS_AUSLEIHE, verleihkarte);
            if (istVorgemerkt(medium))
            {
                loescheVormerkkarte(medium, ausleihDatum, kunde);
            }
        }
        // Was passiert wenn das Protokollieren mitten in der Schleife
        // schief geht? informiereUeberAenderung in einen finally Block?
        informiereUeberAenderung();
    }

    @Override
    public boolean kundeImBestand(Kunde kunde)
    {
        return _kundenstamm.enthaeltKunden(kunde);
    }

    @Override
    public boolean mediumImBestand(Medium medium)
    {
        return _medienbestand.enthaeltMedium(medium);
    }

    @Override
    public boolean medienImBestand(List<Medium> medien)
    {
        assert medien != null : "Vorbedingung verletzt: medien != null";
        assert !medien.isEmpty() : "Vorbedingung verletzt: !medien.isEmpty()";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!mediumImBestand(medium))
            {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Medium> getAusgelieheneMedienFuer(Kunde kunde)
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        List<Medium> result = new ArrayList<Medium>();
        for (Verleihkarte verleihkarte : _verleihkarten.values())
        {
            if (verleihkarte.getEntleiher()
                .equals(kunde))
            {
                result.add(verleihkarte.getMedium());
            }
        }
        return result;
    }

    @Override
    public Kunde getEntleiherFuer(Medium medium)
    {
        assert istVerliehen(
                medium) : "Vorbedingung verletzt: istVerliehen(medium)";
        Verleihkarte verleihkarte = _verleihkarten.get(medium);
        return verleihkarte.getEntleiher();
    }

    @Override
    public Verleihkarte getVerleihkarteFuer(Medium medium)
    {
        assert istVerliehen(
                medium) : "Vorbedingung verletzt: istVerliehen(medium)";
        return _verleihkarten.get(medium);
    }

    @Override
    public List<Verleihkarte> getVerleihkartenFuer(Kunde kunde)
    {
        assert kundeImBestand(
                kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        List<Verleihkarte> result = new ArrayList<Verleihkarte>();
        for (Verleihkarte verleihkarte : _verleihkarten.values())
        {
            if (verleihkarte.getEntleiher()
                .equals(kunde))
            {
                result.add(verleihkarte);
            }
        }
        return result;
    }

    @Override
    public void merkeVor(Kunde kunde, List<Medium> medien, Datum vormerkDatum)
            throws ProtokollierException
    {
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        assert vormerkDatum != null : "Vorbedingung verletzt: vormerkDatum ist null";
        assert istVormerkenMoeglich(kunde, medien) : "Vorbedingung verletzt:  istVormerkenMoeglich(kunde, medien)";
        
        for (Medium medium : medien)
        {
            Vormerkkarte vormerkkarte = new Vormerkkarte(kunde, medium,
                    vormerkDatum);

            _vormerkkarten.get(medium).add(vormerkkarte);
            _protokollierer.protokolliere(
                    VerleihProtokollierer.EREIGNIS_VORMERKUNG, vormerkkarte);
        }
        informiereUeberAenderung();
    }

    @Override
    public boolean istVormerkenMoeglich(Kunde kunde, List<Medium> medien)
    {
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        
        for (Medium medium : medien)
        {
            //TODO ggf. weitere Methode istAlleVormerkenMoeglich einfügen, da hier sonst wegen einer einzigen vorhandenen
            //Vormerkung des Kunden der Prozess nicht durchführbar ist, evtl. unschön
            if (istVorgemerktVon(kunde, medium) || getVormerkkartenFuer(medium).size() >= 3
                    || istVerliehenAn(kunde, medium))
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public List<Vormerkkarte> getVormerkkartenFuer(Medium medium)
    {
        return _vormerkkarten.get(medium);
    }

    @Override
    public List<Kunde> getVormerkerFuer(Medium medium)
    {
        List<Kunde> result = new ArrayList<Kunde>();
        for (Vormerkkarte karte : _vormerkkarten.get(medium))
        {
            result.add(karte.getVormerker());
        }
        return result;
    }

    @Override
    public List<Medium> getVorgemerkteMedienFuer(Kunde kunde)
    {
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        
        List<Medium> result = new ArrayList<Medium>();
        for (List<Vormerkkarte> liste : _vormerkkarten.values())
        {
            for (Vormerkkarte karte : liste)
            {
                if (karte.getVormerker().equals(kunde))
                {
                    result.add(karte.getMedium());
                }
            }
        }
        return result;
    }

    @Override
    public List<Vormerkkarte> getAlleVormerkkarten()
    {
        List<Vormerkkarte> result = new ArrayList<Vormerkkarte>();
        for (List<Vormerkkarte> liste : _vormerkkarten.values())
        {
            result.addAll(liste);
            //TODO alternativ:
//            for(Vormerkkarte karte : liste)
//            {
//                result.add(karte);
//            }
        }
        return result;
    }

    @Override
    public void loescheVormerkkarten(List<Medium> medien, Datum loeschDatum,
            Kunde kunde) throws ProtokollierException
    {
        assert sindAlleVorgemerktVon(kunde, medien) : "Vorbedingung verletzt: sindAlleVorgemerktVon(kunde,";
        assert loeschDatum != null : "Vorbedingung verletzt: loeschDatum ist null";
        
        for (Medium medium : medien)
        {
            loescheVormerkkarte(medium, loeschDatum, kunde);
        }
        informiereUeberAenderung();        
    }
    
    @Override
    public void loescheVormerkkarte(Medium medium, Datum loeschDatum, Kunde kunde)
        throws ProtokollierException
        {
            assert istVorgemerktVon(kunde, medium) : "Vorbedingung verletzt: istVorgemerktVon(kunde, medium)";
            
            Vormerkkarte karte = getVormerkkarteFuerKundeUndMedium(kunde, medium);
            _vormerkkarten.get(medium).remove(getVormerkkarteFuerKundeUndMedium(kunde, medium));
            _protokollierer.protokolliere(VerleihProtokollierer.EREIGNIS_VORMERKUNGENTFERNT, karte);
        }

    @Override
    public boolean istVorgemerkt(Medium medium)
    {
        assert mediumImBestand(medium) : "Vorbedingung verletzt: mediumImBestand(medium)";
        
        if (_vormerkkarten.get(medium).size() > 0)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean sindAlleNichtVorgemerkt(List<Medium> medien)
    {
        assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        
        boolean result = true;
        
        for (Medium medium : medien)
        {
            if (istVorgemerkt(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean sindAlleVorgemerkt(List<Medium> medien)
    {
        assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVorgemerkt(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean sindAlleVorgemerktVon(Kunde kunde, List<Medium> medien)
    {
        assert medienImBestand(medien) : "Vorbedingung verletzt: medienImBestand(medien)";
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        
        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVorgemerktVon(kunde, medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean istVorgemerktVon(Kunde kunde, Medium medium)
    {
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: kundeImBestand(kunde)";
        assert mediumImBestand(medium) : "Vorbedingung verletzt: mediumImBestand(medium)";
        
        for (Kunde vkunde : getVormerkerFuer(medium))
        {
            if (vkunde.equals(kunde))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean istErstvormerker(Kunde kunde, Medium medium)
    {
        assert medium != null : "Vorbedingung verletzt: medium ist null";
        assert kunde != null : "Vorbedingung verletzt: kunde ist null";
        
        if (!istVorgemerktVon(kunde, medium))
        {
            return false;
        }
        if (_vormerkkarten.get(medium).get(0).equals(getVormerkkarteFuerKundeUndMedium(kunde, medium)))
        {
            return true;
        }
        return false;
        //nicht möglich, da Datum nur Tagesvergleich anstellt, zu ungenau, wenn mehrere Kunden
        //an einem Tag dasselbe Medium vormerken:
//        Vormerkkarte karte = getVormerkkarteFuerKundeUndMedium(kunde, medium);
//        for (Vormerkkarte vergleichkarte : _vormerkkarten.get(medium))
//        {
//            karte.getVormerkdatum().super.compareTo(vergleichkarte.getVormerkdatum());
//        }
    }

    @Override
    public Vormerkkarte getVormerkkarteFuerKundeUndMedium(Kunde kunde,
            Medium medium)
    {
        assert istVorgemerktVon(kunde, medium) : "Vorbedingung verletzt: istVorgemerktVon(kunde, medium)";
        
        //muss initialisiert werden, Überschreibung result = karte ist jedoch durch Vorbedingung gesichert
        Vormerkkarte result = new Vormerkkarte(kunde, medium, Datum.heute());
        for (Vormerkkarte karte : _vormerkkarten.get(medium))
        {
            if (karte.getVormerker().equals(kunde)) 
            {
                result = karte;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean istFuerAlleErstvormerker(Kunde kunde, List<Medium> medien)
    {
        assert medien != null : "Vorbedingung verletzt: medien ist null";
        assert kunde != null : "Vorbedingung verletzt: kunde ist null";
        
        for (Medium medium : medien)
        {
            if (!istErstvormerker(kunde, medium))
            {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Kunde getErstvormerkerFuer(Medium medium)
    {
        assert medium != null : "Vorbedingung verletzt: medium != null";
        assert istVorgemerkt(medium) : "Vorbedingung verletzt: istVorgemerkt(medium)";
        
        return getVormerkerFuer(medium).get(0);
    }
    

}
