package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connection.controller.ConnectionController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import models.Nivel;
import models.TabelaColuna;

public class TblNiveisDAOImpl implements TblNiveisDAO {

	private Integer idTabela = 3;

	private String chavePrimaria = "id_nivel";
	
	@Override
	public Nivel getById(Integer id) {
		Nivel tempNivel = null;
		Connection con = ConnectionController.getConexaoMySQL();
		try {
			String tbl = getTblName();
			String sql = "SELECT id_nivel, nome_nivel FROM " + tbl + " WHERE id_nivel = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Integer tempId = rs.getInt("id_nivel");
				String tempName = rs.getString("nome_nivel");
				tempNivel = new Nivel(tempId, tempName);
			}
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return tempNivel;
	}

	@Override
	public List<Nivel> getAll() {
		Nivel tempNivel = null;
		Connection con = ConnectionController.getConexaoMySQL();
		List<Nivel> listaNiveis = new ArrayList<Nivel>();
		try {
			String tbl = getTblName();
			String sql = "SELECT * FROM " + tbl;
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Integer tempId = rs.getInt("id_nivel");
				String tempName = rs.getString("nome_nivel");
				tempNivel = new Nivel(tempId, tempName);
				listaNiveis.add(tempNivel);
			}
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return listaNiveis;
	}

	@Override
	public Integer insert(Nivel nivel) {
		Connection con = ConnectionController.getConexaoMySQL();
		Integer idGerado = null;
		try {
			String tbl = getTblName();
			String sql = "INSERT INTO " + tbl + " (nome_nivel) VALUES (?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, nivel.getNome());

			ps.executeUpdate();
			
			  // Pega o ID gerado
	        ResultSet rs = ps.getGeneratedKeys();
	        if (rs.next()) {
	            idGerado = rs.getInt(1); // ou rs.getObject(1) se quiser genérico
	        }

	        rs.close();
	        ps.close();
	        con.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return idGerado;
	}

	@Override
	public Integer updateById(Nivel nivel) {
		Connection con = ConnectionController.getConexaoMySQL();
		Integer result = 0;
		try {
			String tbl = getTblName();
			String sql = "UPDATE " + tbl + " SET nome_nivel = (?) WHERE id_nivel = (?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, nivel.getNome());
			ps.setInt(2, nivel.getCodigo());

			result = ps.executeUpdate();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return result; // retorna a quantidade de linhas afetadas, nao o ID
	}

	@Override
	public Integer deleteById(Nivel nivel) {
		Connection con = ConnectionController.getConexaoMySQL();
		Integer result = 0;
		try {
			String tbl = getTblName();
			String sql = "DELETE from " + tbl + " WHERE id_nivel = (?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, nivel.getCodigo());

			result = ps.executeUpdate();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return result; // retorna a quantidade de linhas afetadas, nao o ID
	}

	@Override
	public TableView estruturaTbl() {

		ObservableList<ObservableList> data = FXCollections.observableArrayList();
		Connection con = ConnectionController.getConexaoMySQL();
		TableView tableview = new TableView<>();
		try {
			String tbl = getTblName();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tbl);

			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));

				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {

							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				tableview.getColumns().addAll(col);
				System.out.println("Column [" + i + "] ");
			}

			while (rs.next()) {
				// Iterate Row
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column
					row.add(rs.getString(i));
				}
				System.out.println("Row [1] added " + row);
				data.add(row);
			}
			// FINALLY ADDED TO TableView
			tableview.setItems(data);
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error on Building Data");
		}
		return tableview;
	}

	@Override
	public ObservableList<String> obterNomesDasColunas() {
		ObservableList<String> nomesDasColunas = FXCollections.observableArrayList();

		try (Connection con = ConnectionController.getConexaoMySQL();Statement stmt = con.createStatement();) {
			String tbl = getTblName();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tbl);
			int colCount = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				nomesDasColunas.add(rs.getMetaData().getColumnName(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erro ao obter nomes das colunas.");
		}

		return nomesDasColunas;
	}

	@Override
	public String getTblName(){
		Connection con = ConnectionController.getConexaoMySQL();
		String nameCurrentTbl = "";
		try {
			String tbls = "";
			DicTabelasDAO tblsdao = new DicTabelasDAOImpl();
			tbls = tblsdao.getTblName();
			String sql = "SELECT nome_tbl FROM " + tbls + " WHERE id_tbl = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, this.idTabela);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				nameCurrentTbl = rs.getString("nome_tbl");
			}
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return nameCurrentTbl;
	}

	@Override
	public Nivel getByName(String name){
		Nivel tempNivel = null;
		Connection con = ConnectionController.getConexaoMySQL();
		try {
			String tbl = getTblName();
			String sql = "SELECT id_nivel, nome_nivel FROM " + tbl + " WHERE nome_nivel = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Integer tempId = rs.getInt("id_nivel");
				String tempName = rs.getString("nome_nivel");
				tempNivel = new Nivel(tempId, tempName);
			}
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.getMessage();
		}
		return tempNivel;
	}
	
	@Override
	public List getColunasDaTabela(){
		List<TabelaColuna> colunas = new ArrayList<>();
		Connection con = ConnectionController.getConexaoMySQL();
		try {
			String sql = "SELECT * FROM " + getTblName() + " LIMIT 0";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				String nome = meta.getColumnName(i);
				int tipo = meta.getColumnType(i);
				colunas.add(new TabelaColuna(nome, null, tipo));
			}
		}catch (SQLException exception) {
			exception.getMessage();
		}
		return colunas;
	}

	@Override
	public String getChavePrimaria(){
		return this.chavePrimaria;
	}
	
	@Override
	public List<TabelaColuna> mapperEntityToView(Integer id, List<TabelaColuna> estrutura) {
		Nivel tempNivel = getById(id);
		for (TabelaColuna x : estrutura) {
			if (x.getNome().compareTo("id_nivel") == 0) {
				x.setValor(tempNivel.getCodigo());
			}else if (x.getNome().compareTo("nome_nivel") == 0) {
				x.setValor(tempNivel.getNome());
			}
		}
		return estrutura;
	
	}
	@Override
	public Nivel mapperViewToEntity(List<TabelaColuna> estrutura){
		Nivel tempNivel = new Nivel();
		for (TabelaColuna x : estrutura) {
			 if(x.getNome().compareTo("id_nivel") == 0) {
		            if (x.getValor() != null && !x.getValor().toString().isBlank()) {
		            	tempNivel.setCodigo(Integer.parseInt(x.getValor().toString()));
		            }
			}else if (x.getNome().compareTo("nome_nivel") == 0) {
				tempNivel.setNome(x.getValor().toString());
			}
		}
		return tempNivel;
	
	}
}
