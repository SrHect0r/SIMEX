using System;
using System.Collections.Generic;

namespace LogiTrackAPI.Models;

public partial class Usuari
{
    public int Id { get; set; }

    public string Correu { get; set; } = null!;

    public string Contrasenya { get; set; } = null!;

    public string Nom { get; set; } = null!;

    public string Cognoms { get; set; } = null!;

    public int RolId { get; set; }

    public virtual ICollection<Oferte> Ofertes { get; set; } = new List<Oferte>();

    public virtual Rol Rol { get; set; } = null!;
}
