using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using LogiTrackAPI.Models;

namespace LogiTrackAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuarisController : ControllerBase
    {
        private readonly Simex10Context _context;
        public UsuarisController(Simex10Context context) { _context = context; }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Usuari>>> GetUsuaris()
            => await _context.Usuaris.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Usuari>> GetUsuari(int id)
        {
            var usuari = await _context.Usuaris.FindAsync(id);
            return usuari == null ? NotFound() : usuari;
        }

        [HttpPost("login")]
        public async Task<ActionResult<Usuari>> Login([FromBody] LoginRequest request)
        {
            var usuari = await _context.Usuaris
                .FirstOrDefaultAsync(u => u.Correu == request.Correu && u.Contrasenya == request.Contrasenya);
            return usuari == null ? Unauthorized() : Ok(usuari);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutUsuari(int id, Usuari usuari)
        {
            if (id != usuari.Id) return BadRequest();
            _context.Entry(usuari).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }

    public class LoginRequest
    {
        public string Correu { get; set; } = null!;
        public string Contrasenya { get; set; } = null!;
    }
}