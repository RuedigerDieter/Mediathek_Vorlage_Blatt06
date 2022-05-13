package de.uni_hamburg.informatik.swt.se2.mediathek.services.verleih;

import java.util.List;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Kunde;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Verleihkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.Vormerkkarte;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;
import de.uni_hamburg.informatik.swt.se2.mediathek.services.ObservableService;

/**
 * Der VerleihService erlaubt es, Medien auszuleihen und zurückzugeben.
 * 
 * Für jedes ausgeliehene Medium wird eine neue Verleihkarte angelegt. Wird das
 * Medium wieder zurückgegeben, so wird diese Verleihkarte wieder gelöscht.
 * 
 * VerleihService ist ein ObservableService, als solcher bietet er die
 * Möglichkeit über Verleihvorgänge zu informieren. Beobachter müssen das
 * Interface ServiceObserver implementieren.
 * 
 * @author SE2-Team
 * @version SoSe 2021
 */
public interface VerleihService extends ObservableService
{
    /**
     * Verleiht Medien an einen Kunden. Dabei wird für jedes Medium eine neue
     * Verleihkarte angelegt.
     * 
     * @param kunde Ein Kunde, an den ein Medium verliehen werden soll
     * @param medien Die Medien, die verliehen werden sollen
     * @param ausleihDatum Der erste Ausleihtag
     * 
     * @throws ProtokollierException Wenn beim Protokollieren des
     *             Verleihvorgangs ein Fehler auftritt.
     * 
     * @require kundeImBestand(kunde)
     * @require sindAlleNichtVerliehen(medien)
     * @require ausleihDatum != null
     * @require istFuerAlleErstvormerker(kunde, medien)
     *  
     * @ensure sindAlleVerliehenAn(kunde, medien)
     */
    void verleiheAn(Kunde kunde, List<Medium> medien, Datum ausleihDatum)
            throws ProtokollierException;

    /**
     * Prüft ob die ausgewählten Medium für den Kunde ausleihbar sind
     * 
     * @param kunde Der Kunde für den geprüft werden soll
     * @param medien Die medien
     * 
     * 
     * @return true, wenn das Entleihen für diesen Kunden möglich ist, sonst
     *         false
     * 
     * @require kundeImBestand(kunde)
     * @require medienImBestand(medien)
     */
    boolean istVerleihenMoeglich(Kunde kunde, List<Medium> medien);

    /**
     * Liefert den Entleiher des angegebenen Mediums.
     * 
     * @param medium Das Medium.
     * 
     * @return Den Entleiher des Mediums.
     * 
     * @require istVerliehen(medium)
     * 
     * @ensure result != null
     */
    Kunde getEntleiherFuer(Medium medium);

    /**
     * Liefert alle Medien, die von dem gegebenen Kunden ausgeliehen sind.
     * 
     * @param kunde Der Kunde.
     * @return Alle Medien, die von dem gegebenen Kunden ausgeliehen sind.
     *         Liefert eine leere Liste, wenn der Kunde aktuell nichts
     *         ausgeliehen hat.
     * 
     * @require kundeImBestand(kunde)
     * 
     * @ensure result != null
     */
    List<Medium> getAusgelieheneMedienFuer(Kunde kunde);

    /**
     * @return Eine Listenkopie aller Verleihkarten. Für jedes ausgeliehene
     *         Medium existiert eine Verleihkarte. Ist kein Medium verliehen,
     *         wird eine leere Liste zurückgegeben.
     * 
     * @ensure result != null
     */
    List<Verleihkarte> getVerleihkarten();

    /**
     * Nimmt zuvor ausgeliehene Medien zurück. Die entsprechenden Verleihkarten
     * werden gelöscht.
     * 
     * 
     * @param medien Die Medien.
     * @param rueckgabeDatum Das Rückgabedatum.
     * 
     * @require sindAlleVerliehen(medien)
     * @require rueckgabeDatum != null
     * 
     * @ensure sindAlleNichtVerliehen(medien)
     */
    void nimmZurueck(List<Medium> medien, Datum rueckgabeDatum)
            throws ProtokollierException;

    /**
     * Prüft ob das angegebene Medium verliehen ist.
     * 
     * @param medium Ein Medium, für das geprüft werden soll ob es verliehen
     *            ist.
     * @return true, wenn das gegebene medium verliehen ist, sonst false.
     * 
     * @require mediumImBestand(medium)
     */
    boolean istVerliehen(Medium medium);

    /**
     * Prüft ob alle angegebenen Medien nicht verliehen sind.
     * 
     * @param medien Eine Liste von Medien.
     * @return true, wenn alle gegebenen Medien nicht verliehen sind, sonst
     *         false.
     * 
     * @require medienImBestand(medien)
     */
    boolean sindAlleNichtVerliehen(List<Medium> medien);

    /**
     * Prüft ob alle angegebenen Medien verliehen sind.
     * 
     * @param medien Eine Liste von Medien.
     * 
     * @return true, wenn alle gegebenen Medien verliehen sind, sonst false.
     * 
     * @require medienImBestand(medien)
     */
    boolean sindAlleVerliehen(List<Medium> medien);

    /**
     * Prüft, ob alle angegebenen Medien an einen bestimmten Kunden verliehen
     * sind.
     * 
     * @param kunde Ein Kunde
     * @param medien Eine Liste von Medien
     * @return true, wenn alle Medien an den Kunden verliehen sind, sonst false.
     * 
     * @require kundeImBestand(kunde)
     * @require medienImBestand(medien)
     */
    boolean sindAlleVerliehenAn(Kunde kunde, List<Medium> medien);

    /**
     * Prüft, ob ein Medium an einen bestimmten Kunden verliehen ist.
     * 
     * @param kunde Ein Kunde
     * @param medium Ein Medium
     * @return true, wenn das Medium an den Kunden verliehen ist, sonst false.
     * 
     * @require kundeImBestand(kunde)
     * @require mediumImBestand(medium)
     */
    boolean istVerliehenAn(Kunde kunde, Medium medium);

    /**
     * Prüft ob der angebene Kunde existiert. Ein Kunde existiert, wenn er im
     * Kundenstamm enthalten ist.
     * 
     * @param kunde Ein Kunde.
     * @return true wenn der Kunde existiert, sonst false.
     * 
     * @require kunde != null
     */
    boolean kundeImBestand(Kunde kunde);

    /**
     * Prüft ob das angebene Medium existiert. Ein Medium existiert, wenn es im
     * Medienbestand enthalten ist.
     * 
     * @param medium Ein Medium.
     * @return true wenn das Medium existiert, sonst false.
     * 
     * @require medium != null
     */
    boolean mediumImBestand(Medium medium);

    /**
     * Prüft ob die angebenen Medien existierien. Ein Medium existiert, wenn es
     * im Medienbestand enthalten ist.
     * 
     * @param medien Eine Liste von Medien.
     * @return true wenn die Medien existieren, sonst false.
     * 
     * @require medien != null
     * @require !medien.isEmpty()
     */
    boolean medienImBestand(List<Medium> medien);

    /**
     * Gibt alle Verleihkarten für den angegebenen Kunden zurück.
     * 
     * @param kunde Ein Kunde.
     * @return Alle Verleihkarten des angebenen Kunden. Eine leere Liste, wenn
     *         der Kunde nichts entliehen hat.
     * 
     * @require kundeImBestand(kunde)
     * 
     * @ensure result != null
     */
    List<Verleihkarte> getVerleihkartenFuer(Kunde kunde);

    /**
     * Gibt die Verleihkarte für das angegebene Medium zurück, oder null wenn
     * das Medium nicht verliehen ist.
     * 
     * @param medium Ein Medium.
     * @return Die Verleihkarte für das angegebene Medium.
     * 
     * @require istVerliehen(medium)
     * 
     * @ensure (result != null)
     */
    Verleihkarte getVerleihkarteFuer(Medium medium);
    
    //-------------------------------------------------------------------------------
    
    /**
     * Merkt Medien für einen Kunden vor. Dabei wird für jedes Medium eine neue
     * Vormerkkarte angelegt.
     * 
     * @param kunde Ein Kunde, für den ein Medium vorgemerkt werden soll
     * @param medien Die Medien, die vorgemerkt werden sollen
     * @param vormerkDatum Der erste Vormerktag
     * 
     * @throws ProtokollierException Wenn beim Protokollieren des
     *             Vormerkvorgangs ein Fehler auftritt.
     * 
     * @require kundeImBestand(kunde)
     * @require istVormerkenMoeglich(medien)
     * @require vormerkDatum != null
     * @require istVormerkenMoeglich(kunde, medien)
     * 
     * @ensure sindAlleVerliehenAn(kunde, medien)
     */
    void merkeVor(Kunde kunde, List<Medium> medien, Datum vormerkDatum)
            throws ProtokollierException;
    
    /**
     * Prüft ob die ausgewählten Medium für den Kunde vormerkbar sind
     * 
     * @param kunde Der Kunde für den geprüft werden soll
     * @param medien Die medien
     * 
     * 
     * @return true, wenn das Vormerken für diesen Kunden möglich ist, sonst
     *         false
     * 
     * @require kundeImBestand(kunde)
     * @require medienImBestand(medien)
     */
    boolean istVormerkenMoeglich(Kunde kunde, List<Medium> medien);
    
    /**
     * Gibt die Vormerkkarten für das angegebene Medium zurück, oder null wenn
     * das Medium nicht vorgemerkt ist.
     * 
     * @param medium Ein Medium.
     * @return Eine Liste von Vormerkkarten des angegebenen Mediums.Wenn keine 
     *  Vormerkungen bestehen, wird eine leere Liste ausgegeben.
     * 
     * @ensure (result != null)
     */
    List<Vormerkkarte> getVormerkkartenFuer(Medium medium);
    
    /**
     * TODO wird diese Operation benötigt? ggf löschen
     * Liefert die Kunden, die das angegebene Medium vorgemerkt haben.
     * 
     * @param medium Das Medium.
     * 
     * @return Liste der Kunden, welche das Medium vorgemerkt haben. Wenn keine 
     *  Vormerkungen bestehen, wird eine leere Liste ausgegeben.
     * @ensure result != null
     */
    List<Kunde> getVormerkerFuer(Medium medium);
     
    /**
     * Liefert alle Medien, die von dem gegebenen Kunden vorgemerkt sind.
     * 
     * @param kunde Der Kunde.
     * @return Alle Medien, die von dem gegebenen Kunden vorgemerkt sind.
     *         Liefert eine leere Liste, wenn der Kunde aktuell nichts
     *         vorgemerkt hat.
     * 
     * @require kundeImBestand(kunde)
     * 
     * @ensure result != null
     */
    List<Medium> getVorgemerkteMedienFuer(Kunde kunde);
    
    /**
     * @return Eine Listenkopie aller Vormerkkarten. Für jede Vormerkung
     *         existiert eine Vormerkkarte. Ist keine Vormerkung getätigt worden, wird eine leere Liste 
     *         zurückgegeben.
     * 
     * @ensure result != null
     */
    List<Vormerkkarte> getAlleVormerkkarten();
    
    /**
     * TODO Datum wird aktuell nicht benötigt, ggf entfernen
     * Loescht die korrespondierende Vormerkung zwischen Medium und Kunde und protokolliert dieses.
     * @param medium Das Medium.
     * @param loeschDatum Das Datum.
     * @param kunde Der Kunde.
     * @throws ProtokollierException Wenn beim Protokollieren des
     *             Loeschvorgangs ein Fehler auftritt.
     *             
     * @require istVorgemerktVon(kunde, medium)
     * @require loeschDatum != null
     * 
     * @ensure !istVorgemerktVon(kunde, medium)
     */
    void loescheVormerkkarte(Medium medium, Datum loeschDatum, Kunde kunde) throws ProtokollierException;
    
    /**
     * Loescht die zuvor getätigten Vormerkungen eines Kunden bzgl. einer Liste von Medien.
     * 
     * 
     * @param medien Die Medien.
     * @param loeschDatum Das Löschdatum.
     * @param kunde Der Kunde.
     * 
     * @require sindAlleVorgemerktVon(kunde, medien)
     * @require loeschDatum != null
     * @throws ProtokollierException Wenn beim Protokollieren des
     *             Loeschvorgangs ein Fehler auftritt.
     * 
     * @ensure !sindAlleVorgemerktVon(kunde, medien)
     */
    void loescheVormerkkarten(List<Medium> medien, Datum loeschDatum, Kunde kunde)
            throws ProtokollierException;
    
    /**
     * Prüft ob das angegebene Medium vorgemerkt ist.
     * 
     * @param medium Ein Medium, für das geprüft werden soll ob es vorgemerkt
     *            ist.
     * @return true, wenn das gegebene medium vorgemerkt ist, sonst false.
     * 
     * @require mediumImBestand(medium)
     */
    boolean istVorgemerkt(Medium medium);
    
    /**
     * Prüft ob alle angegebenen Medien nicht vorgemerkt sind.
     * 
     * @param medien Eine Liste von Medien.
     * @return true, wenn alle gegebenen Medien nicht vorgemerkt sind, sonst
     *         false.
     * 
     * @require medienImBestand(medien)
     */
    boolean sindAlleNichtVorgemerkt(List<Medium> medien);
    
    /**
     * TODO wirklich benötigt? ggf löschen
     * Prüft ob alle angegebenen Medien vorgemerkt sind.
     * 
     * @param medien Eine Liste von Medien.
     * 
     * @return true, wenn alle gegebenen Medien vorgemerkt sind, sonst false.
     * 
     * @require medienImBestand(medien)
     */
    boolean sindAlleVorgemerkt(List<Medium> medien);
    
    /**
     * Prüft, ob alle angegebenen Medien für einen bestimmten Kunden vorgemerkt
     * sind.
     * 
     * @param kunde Ein Kunde
     * @param medien Eine Liste von Medien
     * @return true, wenn alle Medien für den Kunden vorgemerkt sind, sonst false.
     * 
     * @require kundeImBestand(kunde)
     * @require medienImBestand(medien)
     */
    boolean sindAlleVorgemerktVon(Kunde kunde, List<Medium> medien);
    
    /**
     * Prüft, ob ein Medium für einen bestimmten Kunden vorgemerkt ist.
     * 
     * @param kunde Ein Kunde
     * @param medium Ein Medium
     * @return true, wenn das Medium für den Kunden vorgemerkt ist, sonst false.
     * 
     * @require kundeImBestand(kunde)
     * @require mediumImBestand(medium)
     */
    boolean istVorgemerktVon(Kunde kunde, Medium medium);
    
    /**
     * Prüft, ob der Kunde derjenige ist, welcher am längsten Vormerker ist.
     * @param kunde Kunde, der geprüft werden soll
     * @param medium Medium für das geprüft werden soll
     * @return true, wenn Kunde Erstvormerker ist, 
     *      false, sonst
     *      
     * @require kunde != null
     * @require medium != null
     */
    boolean istErstvormerker(Kunde kunde, Medium medium);
    
    /**
     * Prüft, ob Kunde für alle gewählten Medien Erstvormerker ist
     * @param kunde Kunde, der geprüft werden soll
     * @param medien Medien, die geprüft werden sollen
     * @return true, wenn Kunde für alle Medien Erstvormerker ist,
     *      false, sonst
     * 
     * @require kunde != null
     * @require medien != null
     */
    boolean istFuerAlleErstvormerker(Kunde kunde, List<Medium> medien);
    
    /**
     *  Gibt die Vormerkkarte aus, welche mit spezifiziertem Kunden und Medium assoziiert ist.
     * @param kunde Kunde auf der Vormerkkarte
     * @param medium Medium der Vormerkkarte
     * @return Vormerkkarte bzgl Kunde und Medium
     * 
     * @require istVorgemerktVon(kunde, medium)
     * 
     * @ensure result != null
     */
    Vormerkkarte getVormerkkarteFuerKundeUndMedium (Kunde kunde, Medium medium);

}
