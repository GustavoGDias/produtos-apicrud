package com.ptbdams.apicrud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptbdams.apicrud.model.Produto;
import com.ptbdams.apicrud.repository.CategoriaRepository;
import com.ptbdams.apicrud.repository.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Produto salvar(Produto produto) {
        if (produtoRepository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new IllegalArgumentException("Produto com este nome já existe");
        }

        validarRegrasNegocio(produto);

        if (!categoriaRepository.existsById(produto.getCategoria().getId())) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }

        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produto) {
        Produto existente = produtoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getNome().equalsIgnoreCase(existente.getNome()) && produtoRepository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new IllegalArgumentException("Outro produto com este nome já existe");
        }

        validarRegrasNegocio(produto);

        existente.setNome(produto.getNome());
        existente.setPreco(produto.getPreco());
        existente.setCategoria(produto.getCategoria());

        return produtoRepository.save(existente);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.buscarPorNome(nome);
    }

    public List<Produto> buscarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId);
    }

    public Produto obterProdutoPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }

    public double calcularPrecoComDesconto(Long id, double percentual) {
        if (percentual > 50.0) {
            throw new IllegalArgumentException("Desconto não pode ser superior a 50%");
        }

        Produto produto = obterProdutoPorId(id);
        return produto.getPreco() * (1 - percentual / 100);
    }

    public void deletarPorId(Long id) {
    if (!produtoRepository.existsById(id)) {
        throw new EntityNotFoundException("Produto não encontrado");
    }
    produtoRepository.deleteById(id);
}

    private void validarRegrasNegocio(Produto produto) {
        if (produto.getPreco() > 10000.0) {
            throw new IllegalArgumentException("Preço não pode ser maior que R$10.000,00");
        }

        if (produto.getNome().toLowerCase().contains("promoção") && produto.getPreco() >= 500.0) {
            throw new IllegalArgumentException("Produtos com 'Promoção' no nome devem custar menos de R$500,00");
        }
    }
}