package com.app.cardapio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.cardapio.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Inicialize os gráficos
        BarChart barChart = view.findViewById(R.id.bar_chart);
        PieChart pieChart = view.findViewById(R.id.pie_chart);

        // Configuração do gráfico de barras (frequência de alunos por mês) e de pizza (avaliações do restaurante)
        setupBarChart(barChart);
        setupPieChart(pieChart);

        return view;
    }

    private void setupBarChart(BarChart barChart) {
        // Dados fictícios: frequência de alunos por mês
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 120)); // Janeiro
        entries.add(new BarEntry(2, 150)); // Fevereiro
        entries.add(new BarEntry(3, 100)); // Março
        entries.add(new BarEntry(4, 165)); // Abril
        entries.add(new BarEntry(5, 130)); // Maio
        entries.add(new BarEntry(6, 140)); // Junho

        BarDataSet dataSet = new BarDataSet(entries, "Frequência de Alunos");
        dataSet.setColor(getResources().getColor(R.color.green_accent));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true); // Certifique-se de que as barras se ajustem ao espaço do gráfico
        barChart.getDescription().setEnabled(false); // Remove descrição padrão
        barChart.animateY(1000); // Animação ao carregar
    }

    private void setupPieChart(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30, "Péssimo"));
        entries.add(new PieEntry(20, "Ruim"));
        entries.add(new PieEntry(25, "Regular"));
        entries.add(new PieEntry(15, "Bom"));
        entries.add(new PieEntry(10, "Excelente"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getResources().getIntArray(R.array.pie_chart_colors)); // Definir cores personalizadas
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setDrawHoleEnabled(true); // Habilitar circulo no centro
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Avaliações");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false); // Remove descrição padrão
        pieChart.animateY(1000); // Animação ao carregar
    }
}

