using System;
using System.Collections.Generic;

namespace LogiTrackAPI.Models;

public partial class TrackingStep
{
    public int Id { get; set; }

    public int? Ordre { get; set; }

    public string? Nom { get; set; }

    public virtual ICollection<Incoterm> Incoterms { get; set; } = new List<Incoterm>();
}
