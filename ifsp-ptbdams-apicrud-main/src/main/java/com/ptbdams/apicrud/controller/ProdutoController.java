package com.ptbdams.apicrud.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptbdams.apicrud.model.Produto;
import com.ptbdams.apicrud.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Cria um novo produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação ou regras de negócio")
    })
    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        Produto produtoSalvo = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    @Operation(summary = "Atualiza um produto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação ou regras de negócio")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@Parameter(description = "ID do produto") @PathVariable Long id, @Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.atualizar(id, produto));
    }

    @Operation(summary = "Busca produtos por nome (parcial ou completo)")
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorNome(@Parameter(description = "Nome ou parte do nome do produto") @RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarPorNome(nome));
    }

    @Operation(summary = "Calcula o preço com desconto para um produto")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Desconto aplicado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    example = """
                        {
                            "nome": "Notebook Dell Inspiron",
                            "precoOriginal": 3500.00,
                            "descontoAplicado": "10%",
                            "precoFinal": 3150.00
                        }"""
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Desconto inválido ou produto não encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    example = """
                        {
                            "mensagem": "O desconto não pode ser superior a 50%",
                            "erro": "BAD_REQUEST",
                            "status": 400
                        }"""
                )
            )
        )
    })
    @GetMapping("/{id}/desconto")
    public ResponseEntity<Map<String, Object>> calcularDesconto(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id,
            @Parameter(description = "Percentual de desconto (0-50)", example = "10.0") 
            @RequestParam double percentual) {

        Produto produto = produtoService.obterProdutoPorId(id);
        double precoFinal = produtoService.calcularPrecoComDesconto(id, percentual);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("nome", produto.getNome());
        resposta.put("precoOriginal", produto.getPreco());
        resposta.put("descontoAplicado", percentual + "%");
        resposta.put("precoFinal", precoFinal);

        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Remove um produto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Erro na requisição")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(
        @Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
