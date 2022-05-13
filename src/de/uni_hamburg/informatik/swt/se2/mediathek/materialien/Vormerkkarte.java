package de.uni_hamburg.informatik.swt.se2.mediathek.materialien;

import de.uni_hamburg.informatik.swt.se2.mediathek.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.mediathek.materialien.medien.Medium;

/**
 * Die Klasse händelt die Vormerkungen der Medien. Jedes erstellte Objekt der Vormerkkarte ist 
 * dabei eine Vormerkung, verbunden mit einem Medium, einem Vormerkdatum und einem Kunden.
 * 
 * @author Großer Tschungus
 *
 */
public class Vormerkkarte
{
    private Kunde _vormerker;
    private Datum _vormerkdatum;
    private Medium _medium;
    
    /**
     * Initialisert eine neue Vormerkkarte mit den gegebenen Daten.
     * 
     * @param vormerker Ein Kunde, der das Medium vorgemerkt hat.
     * @param vormerkdatum Das Datum, an dem der Kunde das Medium vorgemerkt
     *            hat.
     * @param vormerkmedium Das vorgemerkte Medium.
     * 
     * @require vormerker != null
     * @require vormerkmedium != null
     * @require vormerkdatum != null
     * 
     * @ensure #getVormerker() == vormerker
     * @ensure #getMedium() == vormerkmedium
     * @ensure #getVormerkdatum() == vormerkdatum
     */
    public Vormerkkarte(Kunde vormerker, Medium vormerkmedium, Datum vormerkdatum)
    {
        assert vormerker != null : "Vorbedingung verletzt: Kunde != null";
        assert vormerkdatum != null : "Vorbedingung verletzt: Datum != null";
        assert vormerkmedium != null : "Vorbedingung verletzt: Medium != null";
        
        _vormerker = vormerker;
        _vormerkdatum = vormerkdatum;
        _medium = vormerkmedium;
    }
    
    /**
     * Gibt das Vormerkdatum zurück.
     * 
     * @return Das Vormerkdatum.
     * 
     * @ensure result != null
     */
    public Datum getVormerkdatum()
    {
        return _vormerkdatum;
    }

    /**
     * Gibt den Kunden zurück, welcher das Medium vorgemerkt hat.
     * 
     * @return den Kunden, der das Medium vorgemerkt hat.
     * 
     * @ensure result != null
     */
    public Kunde getVormerker()
    {
        return _vormerker;
    }

    /**
     * Gibt eine String-Darstellung der Vormerkkarte (enhält Zeilenumbrüche)
     * zurück.
     * 
     * @return Eine formatierte Stringrepäsentation der Vormerkkarte. Enthält
     *         Zeilenumbrüche.
     * 
     * @ensure result != null
     */
    public String getFormatiertenString()
    {
        return _medium.getFormatiertenString() + "am "
                + _vormerkdatum.toString() + " verliehen an\n"
                + _vormerker.getFormatiertenString();
    }

    /**
     * Gibt das Medium, dessen Vormerkung auf der Karte vermerkt ist, zurück.
     * 
     * @return Das Medium, dessen Vormerkung auf dieser Karte vermerkt ist.
     * 
     * @ensure result != null
     */
    public Medium getMedium()
    {
        return _medium;
    }

    /**
     * Berechnet die Vormerkdauer in Tagen. Der erste Tag der Vormerkung zählt
     * vollständig als Vormerktag.
     *  
     * @return Die Vormerkdauer in Tagen.
     */
    public int getVormerkdauer()
    {
        return Datum.heute()
            .tageSeit(getVormerkdatum()) + 1;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_vormerkdatum == null) ? 0 : _vormerkdatum.hashCode());
        result = prime * result
                + ((_vormerker == null) ? 0 : _vormerker.hashCode());
        result = prime * result + ((_medium == null) ? 0 : _medium.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj instanceof Vormerkkarte)
        {
            Vormerkkarte other = (Vormerkkarte) obj;

            if (other.getVormerkdatum()
                .equals(_vormerkdatum)
                    && other.getVormerker()
                        .equals(_vormerker)
                    && other.getMedium()
                        .equals(_medium))

                result = true;
        }
        return result;
    }

    @Override
    public String toString()
    {
        return getFormatiertenString();
    }
}
