using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using LogiTrackAPI.Models;

namespace LogiTrackAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class OfertesController : ControllerBase
    {
        private readonly Simex10Context _context;
        public OfertesController(Simex10Context context) { _context = context; }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Oferte>>> GetOfertes()
            => await _context.Ofertes.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Oferte>> GetOferte(int id)
        {
            var oferte = await _context.Ofertes.FindAsync(id);
            return oferte == null ? NotFound() : oferte;
        }

        [HttpGet("client/{clientId}")]
        public async Task<ActionResult<IEnumerable<Oferte>>> GetOfertesByClient(int clientId)
            => await _context.Ofertes.Where(o => o.ClientId == clientId).ToListAsync();

        [HttpPut("{id}")]
        public async Task<IActionResult> PutOferte(int id, Oferte oferte)
        {
            if (id != oferte.Id) return BadRequest();
            _context.Entry(oferte).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpPost]
        public async Task<ActionResult<Oferte>> PostOferte(Oferte oferte)
        {
            _context.Ofertes.Add(oferte);
            await _context.SaveChangesAsync();
            return CreatedAtAction("GetOferte", new { id = oferte.Id }, oferte);
        }

        [HttpPost("{id}/acceptar")]
        public async Task<IActionResult> Acceptar(int id)
        {
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound();
            oferta.EstatOfertaId = 12;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpPost("{id}/rebutjar")]
        public async Task<IActionResult> Rebutjar(int id, [FromBody] RebutjarRequest request)
        {
            var oferta = await _context.Ofertes.FindAsync(id);
            if (oferta == null) return NotFound();
            oferta.EstatOfertaId = 13;
            oferta.RaoRebuig = request.Rao;
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }

    public class RebutjarRequest
    {
        public string Rao { get; set; } = null!;
    }
}