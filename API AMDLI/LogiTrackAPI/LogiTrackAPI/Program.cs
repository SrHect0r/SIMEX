using LogiTrackAPI.Models;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<Simex10Context>(options =>
    options.UseSqlServer("Server=vps-5d4cfa08.vps.ovh.net;Database=simex10;User Id=simex10;Password=@Amli102026;TrustServerCertificate=True;"));

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();