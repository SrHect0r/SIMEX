using System;
using System.Collections.Generic;

namespace LogiTrackAPI.Models;

public partial class TipusIncoterm
{
    public int Id { get; set; }

    public string? Codi { get; set; }

    public string? Nom { get; set; }

    public virtual ICollection<Incoterm> Incoterms { get; set; } = new List<Incoterm>();
}
